package ilp.tutorials.simpledemo.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ilp.tutorials.simpledemo.entity.Cw2.Drone;
import ilp.tutorials.simpledemo.entity.Cw2.DronesForServicePoint;
import ilp.tutorials.simpledemo.entity.Cw2.RestrictedArea;
import ilp.tutorials.simpledemo.entity.Cw2.ServicePoint;

@Service
public class IlpService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private String ilpEndpoint;

    public List<Drone> getDrones(){
        Drone[] drones = restTemplate.getForObject(
            ilpEndpoint + "/drones",
            Drone[].class
        );

        return Arrays.asList(drones);
    }

    public List<DronesForServicePoint> getDronesForServicePointsData() {
        DronesForServicePoint[] servicePoints = restTemplate.getForObject(
            ilpEndpoint + "drones-for-service-points", 
            DronesForServicePoint[].class);

            return Arrays.asList(servicePoints);
    }

    public List<ServicePoint> getServicePoints() {
        ServicePoint[] servicePoints = restTemplate.getForObject(
            ilpEndpoint + "service-points", 
            ServicePoint[].class);

            return Arrays.asList(servicePoints);
    }

    public List<RestrictedArea> getRestrictedAreas() {
        RestrictedArea[] restrictedAreas = restTemplate.getForObject(
            ilpEndpoint + "restricted-areas", 
            RestrictedArea[].class);

            return Arrays.asList(restrictedAreas);
    }
}
