package ilp.tutorials.simpledemo.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ilp.tutorials.simpledemo.controllers.BasicController;
import ilp.tutorials.simpledemo.entity.Cw2.AvailabilityRecord;
import ilp.tutorials.simpledemo.entity.Cw2.DispatchReq;
import ilp.tutorials.simpledemo.entity.Cw2.Drone;
import ilp.tutorials.simpledemo.entity.Cw2.DronesForServicePoint;
import ilp.tutorials.simpledemo.entity.Cw2.MedDispatchRec;
import ilp.tutorials.simpledemo.entity.Cw2.QueryAttributes;
import ilp.tutorials.simpledemo.entity.Cw2.ServicePointDrone;

@Service
public class DroneService {

    private final BasicController basicController;
    
    @Autowired
    private IlpService ilpService;

    DroneService(BasicController basicController) {
        this.basicController = basicController;
    }

    public List<Integer> getDronesWithCooling(boolean state) {
        List<Drone> drones = ilpService.getDrones();

        return drones.stream()
                .filter(d -> d.getCapability().getCooling() == state)
                .map(Drone::getId)
                .collect(Collectors.toList());
    }

    public Drone getDrone(int id) {
        List<Drone> drones = ilpService.getDrones();

        return drones.stream()
                .filter(d -> d.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // Dynamic Query

    public List<Integer> queryByAttribute(String attribute, String value){
        List<Drone> drones = ilpService.getDrones();

        QueryAttributes query = new QueryAttributes();
        query.setAttribute(attribute);
        query.setOperator("=");
        query.setValue(value);

        return drones.stream()
                .filter(d -> attributeMatches(d, query))
                .map(Drone::getId)
                .collect(Collectors.toList());
    }

    public List<Integer> queryByAttribute(List<QueryAttributes> queries){
        List<Drone> drones = ilpService.getDrones();

        return drones.stream()
                .filter(d -> queries.stream().allMatch(q -> attributeMatches(d, q)))
                .map(Drone::getId)
                .collect(Collectors.toList());
    }
    
    private boolean attributeMatches(Drone d, String attribute, String value){
        switch(attribute) {
            case "name":
                return d.getName().equals(value);
            
            case "id":
                return String.valueOf(d.getId()).equals(value);
        }

        var capability = d.getCapability();

        switch (attribute) {
            case "cooling":
                return String.valueOf(capability.getCooling()).equals(value);

            case "heating":
                return String.valueOf(capability.getHeating()).equals(value);

            case "capacity":
                return Double.compare(capability.getCapacity(), Double.parseDouble(value)) == 0;

            case "maxMoves":
                return capability.getMaxMoves() == Integer.parseInt(value);

            case "costPerMove":
                return Double.compare(capability.getCostPerMove(), Double.parseDouble(value)) == 0;

            case "costInitial":
                return Double.compare(capability.getCostInitial(), Double.parseDouble(value)) == 0;

            case "costFinal":
                return Double.compare(capability.getCostFinal(), Double.parseDouble(value)) == 0;
        }

        return false;
    }

    private boolean attributeMatches(Drone drone, QueryAttributes query){
        String attribute = query.getAttribute();
        String operator = query.getOperator();
        String value = query.getValue();

        var capability = drone.getCapability();

        switch (attribute) {
            case "name":
                return compareString(drone.getName(), operator, value);
            
            case "id":
                return compareDouble(drone.getId(), operator, value);

            case "cooling":
                return compareBoolean(capability.getCooling(), operator, value);

            case "heating":
                return compareBoolean(capability.getHeating(), operator, value);

            case "capacity":
                return compareDouble(capability.getCapacity(), operator, value);

            case "maxMoves":
                return compareDouble(capability.getMaxMoves(), operator, value);

            case "costPerMove":
                return compareDouble(capability.getCostPerMove(), operator, value);

            case "costInitial":
                return compareDouble(capability.getCostInitial(), operator, value);

            case "costFinal":
                return compareDouble(capability.getCostFinal(), operator, value);
        }

        return false;
    }

    // Comparison Functions

    private boolean compareBoolean(boolean droneValue, String operator, String value)
    {
        boolean valueBool = Boolean.parseBoolean(value);

        switch(operator)
        {
            case "=":
                return droneValue == valueBool;
            
            case "!=":
                return droneValue =! valueBool;

        }
        
        return false;
    }

    private boolean compareString(String droneValue, String operator, String value)
    {
        switch(operator)
        {
            case "=":
                return droneValue.equals(value);
            
            case "!=":
                return droneValue.equals(value);

        }

        return false;
    }

    private boolean compareDouble(double droneValue, String operator, String value)
    {
        double val = Double.parseDouble(value);

        switch(operator)
        {
            case "=": return Double.compare(droneValue, val) == 0;
            case "!=": return Double.compare(droneValue, val) != 0;
            case "<": return droneValue < val;
            case ">": return droneValue > val;
        }

        return false;
    }

    // Check Availability

    public List<Integer> queryAvailableDrones(List<MedDispatchRec> recs) {

        List<Drone> drones = ilpService.getDrones();

        return drones.stream()
                .filter(d -> isDroneEligible(d, recs))
                .map(Drone::getId)
                .collect(Collectors.toList());

    }

    public boolean meetsRequirements(Drone drone, MedDispatchRec medRec)
    {
        ilp.tutorials.simpledemo.entity.Cw2.Capability capability = drone.getCapability();
        DispatchReq requirements = medRec.getDispatchReq();

        if (capability.getCapacity() < requirements.getCapacity())
            return false;

        if (Boolean.TRUE.equals(requirements.getCooling()) && !capability.getCooling())
            return false;

        if (Boolean.TRUE.equals(requirements.getHeating()) && !capability.getHeating())
            return false;

        return true;

        
    }

    public boolean isDroneAvailableOn(int droneId, LocalDate date, LocalTime time)
    {
        List<DronesForServicePoint> servicePoints = ilpService.getDronesForServicePointsData();

        DayOfWeek targetDay = date.getDayOfWeek();

        for (DronesForServicePoint point:servicePoints) {
            for(ServicePointDrone drone : point.getDrones()) 
            {
                if(drone.getId() == droneId){

                    for (AvailabilityRecord availability : drone.getGetAvailability())
                    {
                        if(availability.getDayOfWeek().equals(targetDay))
                        {
                            if(time.isBefore(availability.getUntil()) && 
                            time.isAfter(availability.getFrom()))
                            {
                                return true;
                            }
                        }
                    }
                }
                
            }
        }

        return false;
    }


    public boolean isDroneEligible(Drone drone, List<MedDispatchRec> recs)
    {
        for (MedDispatchRec rec : recs)
        {
            if (!meetsRequirements(drone, rec))
                return false;

            if (!isDroneAvailableOn(drone.getId(), rec.getDate(), rec.getTime()))
                return false;
        }

        return true;
    }

    public boolean isDroneEligible(Drone drone, MedDispatchRec rec)
    {
        if (!meetsRequirements(drone, rec))
            return false;

        if (!isDroneAvailableOn(drone.getId(), rec.getDate(), rec.getTime()))
            return false;

        return true;
    }
}
