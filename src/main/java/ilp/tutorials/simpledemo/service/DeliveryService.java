package ilp.tutorials.simpledemo.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ilp.tutorials.simpledemo.cache.PathCache;
import ilp.tutorials.simpledemo.entity.Cw1.Position;
import ilp.tutorials.simpledemo.entity.Cw2.Drone;
import ilp.tutorials.simpledemo.entity.Cw2.DronesForServicePoint;
import ilp.tutorials.simpledemo.entity.Cw2.MedDispatchRec;
import ilp.tutorials.simpledemo.entity.Cw2.RestrictedArea;
import ilp.tutorials.simpledemo.entity.Cw2.ServicePoint;
import ilp.tutorials.simpledemo.entity.Cw2.ServicePointDrone;
import ilp.tutorials.simpledemo.entity.Cw2.Delivery.DeliveryPath;
import ilp.tutorials.simpledemo.entity.Cw2.Delivery.DeliveryPlan;
import ilp.tutorials.simpledemo.entity.Cw2.Delivery.DronePath;
import ilp.tutorials.simpledemo.pathing.AStarPathFinder;

@Service
public class DeliveryService {

    @Autowired
    private IlpService ilpService;

    @Autowired
    private DroneService droneService;

    private AStarPathFinder aStar;
    private PathCache pathCache = new PathCache();

    public DeliveryPlan calcDeliveryPath(List<MedDispatchRec> recs)
    {
        
        List<Drone> allDrones = ilpService.getDrones();
        List<ServicePoint> servicePoints = ilpService.getServicePoints();
        List<DronesForServicePoint> droneServicePoint = ilpService.getDronesForServicePointsData();
        List<RestrictedArea> restrictedAreas = ilpService.getRestrictedAreas();

        aStar = new AStarPathFinder(restrictedAreas);

        Map<Integer, List<MedDispatchRec>> feasibleByDrone = computeFeasibleAssignments(allDrones, recs);

        List<Drone> sortedDrones = allDrones.stream()
                    .sorted(Comparator.comparingDouble(d -> d.getCapability().getCostPerMove()))
                    .toList();

        List<DronePath> routes = new ArrayList<>();
        Set<Integer> assingedDispatchIds = new HashSet<>();

        double totalCost = 0.0;
        int totalMoves = 0;

        int i = 1;
        System.out.println("Test");
        for (ServicePoint sp : servicePoints) {
            System.out.println("Before Test " + i);
            Position spPos = new Position(sp.getLocation().getLng(), sp.getLocation().getLat());
            for (MedDispatchRec rec : recs) {
                List<Position> temp = aStar.findPath(rec.getDelivery(), spPos);
                pathCache.put(rec.getDelivery(), spPos, temp);
                pathCache.put(spPos, rec.getDelivery(),temp.reversed());
                System.out.println("Test" + i);
                i++;
            }
        }

        for (Drone drone : sortedDrones)
        {
            List<MedDispatchRec> remainingForThisDrone = feasibleByDrone
                        .getOrDefault(drone.getId(), List.of())
                        .stream()
                        .filter(rec -> !assingedDispatchIds.contains(rec.getId()))
                        .toList();
            if (remainingForThisDrone.isEmpty()) continue;

            Optional<DronePath> routeOptional = buildDronePath(drone, remainingForThisDrone, droneServicePoint);

            if (routeOptional.isPresent())
            {
                DronePath route = routeOptional.get();
                routes.add(route);

                int movesForRoute = route.countMoves();
                double costForRoute = route.computeCost(drone, movesForRoute);

                totalMoves += movesForRoute;
                totalCost += costForRoute;

                route.getDeliveries().forEach(dp -> assingedDispatchIds.add(dp.getDeliveryId()));

            }


        }
        
        return new DeliveryPlan(totalCost, totalMoves, routes);
    }
    public Map<Integer, List<MedDispatchRec>> computeFeasibleAssignments(List<Drone> drones, List<MedDispatchRec> dispatches)
    {
        Map<Integer, List<MedDispatchRec>> map = new HashMap<>();

        for (Drone d : drones)
        {
            List<MedDispatchRec> canDo = new ArrayList<>();
            for (MedDispatchRec rec : dispatches)
            {
                
                if (droneService.isDroneEligible(d, rec))
                {
                    canDo.add(rec);
                }
            }
            map.put(d.getId(), canDo);
        }
        return map;   
    }

    private Optional<DronePath> buildDronePath(Drone drone, List<MedDispatchRec> dispatches, List<DronesForServicePoint> droneServicePoints)
    {
        ServicePoint home = findServicePoint(drone.getId(), droneServicePoints);
        if (home == null) return Optional.empty();

        Position homePos = new Position(home.getLocation().getLng(), home.getLocation().getLat());
        Position current = homePos;

        int maxMoves = drone.getCapability().getMaxMoves();
        int usedMoves = 0;

        List<MedDispatchRec> remaining = new ArrayList<>(dispatches);
        List<DeliveryPath> deliveries = new ArrayList<>();

        while (!remaining.isEmpty()) 
        {
            MedDispatchRec bestRec = null;
            List<Position> bestPathTo = null;
            int bestExtraMoves = Integer.MAX_VALUE;

            for (MedDispatchRec rec : remaining)
            {
                Position target = rec.getDelivery();

                List<Position> pathTo;
                if (pathCache.contains(current, target))
                {
                    pathTo = pathCache.get(current, target);
                } else
                {
                    pathTo = aStar.findPath(current,target);
                }

                List<Position> pathBack;
                if (pathCache.contains(target, homePos))
                {
                    pathBack = pathCache.get(target, homePos);
                } else
                {
                    pathBack = aStar.findPath(target, homePos);
                }

                int movesTo = pathTo.size(); //added hover move
                int movesBack = pathBack.size() - 1;

                int extraMovesIfLast = movesTo + movesBack;

                if (usedMoves + extraMovesIfLast <= maxMoves && extraMovesIfLast < bestExtraMoves)
                {
                    bestExtraMoves = extraMovesIfLast;
                    bestRec = rec;
                    bestPathTo = pathTo;
                }

                if (bestRec == null) break;

                List<Position> segment = new ArrayList<>(bestPathTo);
                //addhover
                Position last = segment.getLast();
                segment.add(last);

                deliveries.add(new DeliveryPath(bestRec.getId(), segment));

                usedMoves += segment.size() - 1;

                current = last;
                remaining.remove(bestRec);
                
            }
            
        }

        if (!deliveries.isEmpty())
        {
            List<Position> backPath;
            if (pathCache.contains(current, homePos))
            {
                backPath = pathCache.get(current, homePos);
            } else
            {
                backPath = aStar.findPath(current, homePos);
            }

            DeliveryPath lastDelivery = deliveries.getLast();
            List<Position> fp = lastDelivery.getFlightPath();

            fp.remove(fp.size() - 1);
            fp.addAll(backPath);

            usedMoves += (backPath.size() - 1);
        }
        
        if (deliveries.isEmpty())
        {
            return Optional.empty();
        }

        return Optional.of(new DronePath(drone.getId(), deliveries));
    }

    private ServicePoint findServicePoint(int droneID, List<DronesForServicePoint> servicePoints)
    {
        for (DronesForServicePoint sp : servicePoints)
        {
            for (ServicePointDrone d : sp.getDrones())
            {
                if (d.getId() == droneID)
                {
                    return ilpService.getServicePoints().stream()
                            .filter(p -> p.getId() == sp.getServicePointId())
                            .findFirst()
                            .orElse(null);
                }
            }
        }

        return null;
    }

}


