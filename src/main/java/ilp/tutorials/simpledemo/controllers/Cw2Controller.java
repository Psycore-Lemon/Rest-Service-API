package ilp.tutorials.simpledemo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ilp.tutorials.simpledemo.entity.Cw2.Drone;
import ilp.tutorials.simpledemo.entity.Cw2.MedDispatchRec;
import ilp.tutorials.simpledemo.entity.Cw2.QueryAttributes;
import ilp.tutorials.simpledemo.entity.Cw2.Delivery.DeliveryPlan;
import ilp.tutorials.simpledemo.service.DeliveryService;
import ilp.tutorials.simpledemo.service.DroneService;

@RequestMapping("/api/v1")
@RestController
public class Cw2Controller {

    final double STEP_DISTANCE = 0.000015;
    final double CLOSE_THRESHOLD = 0.00015;

    @Autowired
    private DroneService droneService;

    @Autowired
    private DeliveryService deliveryService;
    
    // Coursework 2 //

    // Static Query
    @GetMapping("/droneWithCooling/{state}")
    public List<Integer> droneWithCooling(@PathVariable boolean state)
    {
        return droneService.getDronesWithCooling(state);
    }
    
    @GetMapping("/droneDetails/{id}")
    public ResponseEntity<?> droneDetails(@PathVariable int id)
    {
        Drone drone = droneService.getDrone(id);

        if (drone == null)
        {
            return ResponseEntity.status(404).body("Drone not found");
        }
        
        return ResponseEntity.ok(drone);
    }

    // Dynamic Query
    @GetMapping("/queryAsPath/{attribute}/{value}")
    public List<Integer> queryAsPath(@PathVariable String attribute, @PathVariable String value)
    {
        return droneService.queryByAttribute(attribute, value);
    }

    @PostMapping("/query")
    public List<Integer> query(@RequestBody List<QueryAttributes> queries) {
        return droneService.queryByAttribute(queries);
    }

    // Drone Availability Queries
    @PostMapping("/queryAvailableDrones")
    public List<Integer> queryAvailableDrones(@RequestBody List<MedDispatchRec> medDispatchRec) {
        
        return droneService.queryAvailableDrones(medDispatchRec);
    }

    @PostMapping("calcDeliveryPath")
    public DeliveryPlan calcDeliveryPath(@RequestBody List<MedDispatchRec> recs) {
        
        return deliveryService.calcDeliveryPath(recs);
    }
    
    
    // CalcDeliveryPathAsGeoJson
}
