package ilp.tutorials.simpledemo.pathing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import javax.management.RuntimeErrorException;

import ilp.tutorials.simpledemo.entity.Cw2.RestrictedArea;
import ilp.tutorials.simpledemo.pathing.PathingFunctions;
import ilp.tutorials.simpledemo.entity.Cw1.*;

public class AStarPathFinder {

    private static final double STEP_DISTANCE = 0.00015;
    private static final double CLOSE_THRESHOLD = 0.00015;
    private static final double SQR2 = Math.sqrt(2);

    private PathingFunctions pathingService = new PathingFunctions();

    private static final double[] DIRS_DEG =
    {
        0, 22.5, 45, 67.5,
        90, 112.5, 135, 157.5,
        180, 202.5, 225, 247.5,
        270, 292.5, 315, 337.5

        /**
         * 0 = East
         * 90 = North
         * 180 = West
         * 270 = South
         */
    };

    private final List<RestrictedArea> restrictedAreas;
    
    public AStarPathFinder(List<RestrictedArea> restrictedAreas) {
        this.restrictedAreas = restrictedAreas;
    }

    public List<Position> findPath(Position start, Position goal)
    {
        ///TETS
        List<List<Position>> paths = new ArrayList<>();
        List<Position> closedPath = new ArrayList<>();
        /// TEST
        Comparator<Node> byF = Comparator.comparing(n -> n.f);
        PriorityQueue<Node> open = new PriorityQueue<>(byF);
        Set<String> closed = new HashSet<>();
        Map<String, Node> allNodes = new HashMap<>();

        Map<String, Double> bestG = new HashMap<>();

        Node startNode = new Node(start, null, 0, heuristic(start, goal));
        open.add(startNode);
        allNodes.put(key(start), startNode);

        bestG.put(key(start), 0.0);

        int safety = 0;
        while (!open.isEmpty())
        {            
            if (safety > 200_000L)
            {
                String geoJson = pathingService.toCombinedGeoJson(paths, restrictedAreas);
                //String geoJson = pathingService.toGeoJson(closedPath);
                pathingService.saveGeoJsonToFile(geoJson, "Full GeoJson");
                throw new RuntimeException("A* runaway detected");
            }
            safety ++;
            ///////Test area///////
            Node current = open.poll();
            String ck = key(current.pos);

            
            if (closed.contains(ck))
                continue;
            
            closedPath.add(current.pos);
            closed.add(ck);
            if (safety % 10000L ==0)
            {
                paths.add(reconstructPath(current, goal));
                System.out.println(open.size());
            }

            if (pathingService.distanceTo(current.pos, goal) < CLOSE_THRESHOLD)
            {
                List<Position> finalPath = reconstructPath(current, goal);
                paths.add(finalPath);
                String geoJson = pathingService.toCombinedGeoJson(paths, restrictedAreas);
                pathingService.saveGeoJsonToFile(geoJson, "Full GeoJson");
                return finalPath;
            }

            for (Position neighbour : generateNeighbours(current.pos))
            {
                String nk = key(neighbour);
                if (!isValidMove(current.pos, neighbour)) 
                {
                    closed.add(nk);
                    continue;
                }
                if (closed.contains(nk))
                {
                    continue;
                }

                double costToNeighbour = current.g + 1.0;
                Double knownG = bestG.get(nk);
                Node neighbourNode = allNodes.get(nk);

                if (neighbourNode == null)
                {
                    neighbourNode = new Node(neighbour, current, costToNeighbour, heuristic(neighbour, goal));
                    allNodes.put(nk, neighbourNode);
                    open.add(neighbourNode);
                } else
                {
                    if (costToNeighbour < neighbourNode.g)
                    {
                        neighbourNode.parent = current;
                        neighbourNode.g = costToNeighbour;
                        neighbourNode.f = neighbourNode.g + neighbourNode.h;
                        open.add(neighbourNode);
                    }
                }

                bestG.put(nk, costToNeighbour);
                
                
                double f = costToNeighbour + heuristic(neighbour, goal);
                //open.remove(current);
                

            }
        }

        return Collections.emptyList();
    }

    // ==============

        private double heuristic(Position a, Position b)
        {
            return pathingService.distanceTo(a, b) / STEP_DISTANCE;
        }

        private List<Position> reconstructPath(Node lastNodeObj, Position goal)
        {
            Node current = lastNodeObj;
            List<Position> path = new ArrayList<>();
            
            //path.add(goal);
            while (current != null)
            {
                path.add(current.pos);
                current = current.parent;
            }
            Collections.reverse(path);
            return path;
        }

        private String key(Position p)
        {
            return String.format("%.6f_%.6f", p.getLng(), p.getLat());
        }

        private List<Position> generateNeighbours(Position from)
        {
            List<Position> result = new ArrayList<>(16);
            double baseLng = from.getLng();
            double basLat = from.getLat();

            for (double deg : DIRS_DEG)
            {
                double rad = Math.toRadians(deg);
                double dx = STEP_DISTANCE * Math.cos(rad);
                double dy = STEP_DISTANCE * Math.sin(rad);

                double newLng = baseLng + dx;
                double newLat = basLat + dy;
                result.add(new Position(newLng, newLat));
            }

            return result;
        }

        public boolean isValidMove(Position from, Position to)
        {
            double distance = pathingService.distanceTo(from, to);
            if (Math.abs(distance - STEP_DISTANCE) > 1e-6)
            {
                //return false;
            }

            for (RestrictedArea area : restrictedAreas)
            {
                if(pathingService.isInRegion(to, area.getVertices()))
                {
                    return false;
                }
                if (pathingService.segmentIntersectsRegion(from, to, area.getVertices()))
                {
                    //return false;
                }
            }

            return true;
        }
        

    // ==============

    // ==============

    private static class Node
    {
        Position pos;
        Node parent;
        double g;
        double h;
        double f;

        Node(Position pos, Node parent, double g, double h)
        {
            this.pos = pos;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
    
        }

        public double getF() {return g+h;}

        
    }
}
