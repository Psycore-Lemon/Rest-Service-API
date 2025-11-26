package ilp.tutorials.simpledemo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ilp.tutorials.simpledemo.entity.Cw1.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;






@RequestMapping("/api/v1")
@RestController
public class BasicController {

    final double STEP_DISTANCE = 0.000015;
    final double CLOSE_THRESHOLD = 0.00015;

    // Coursework 1 //

    @GetMapping("/uid")
    public String studentId()
    {
        return "s2083752";
    }
    
    @PostMapping("/distanceTo")
    public ResponseEntity<Double> distanceTo(@RequestBody DistanceRequest entity) {
        try
        {
            double lng1 = entity.getPosition1().getLng();
            double lat1 = entity.getPosition1().getLat();
            double lng2 = entity.getPosition2().getLng();
            double lat2 = entity.getPosition2().getLat();

            double distance = Math.sqrt(Math.pow(lng2-lng1, 2) + Math.pow(lat2-lat1,2));

            return ResponseEntity.ok(distance);

        } catch(Exception e)
        {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo (@RequestBody DistanceRequest entity) {
        try
        {
            double lng1 = entity.getPosition1().getLng();
            double lat1 = entity.getPosition1().getLat();
            double lng2 = entity.getPosition2().getLng();
            double lat2 = entity.getPosition2().getLat();

            double distance = Math.sqrt(Math.pow(lng2-lng1, 2) + Math.pow(lat2-lat1,2));
            Boolean isClose = (distance<CLOSE_THRESHOLD) ? true : false;

            return ResponseEntity.ok(isClose);

        } catch(Exception e)
        {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/nextPosition")
    public ResponseEntity<Position> nextPosition(@RequestBody NextPositionRequest entity) {
        try
        {
            Position start = entity.getStart();

            double angleRad = Math.toRadians(entity.getAngle());
            
            double nextLng = start.getLng() + (STEP_DISTANCE * Math.cos(angleRad));
            double nextLat = start.getLat() + (STEP_DISTANCE * Math.sin(angleRad));

            Position nexPosition = new Position(nextLng, nextLat);

            return ResponseEntity.ok(nexPosition);
        } catch(Exception e)
        {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody IsInRegionRequest entity) {
        try
        {
            Position point = entity.getPosition();
            Region region = entity.getRegion();
            double lng = point.getLng();
            double lat = point.getLat();

            List<Position> vertices = region.getVertices();
            
            boolean inside = false;

            if (vertices.size() < 4 || !(vertices.get(0).equals(vertices.get(vertices.size()-1))))
            {
                return ResponseEntity.badRequest().build();
            }
            
            Position p1, p2;
            for (int i = 0; i < vertices.size(); i++)
            {
                p1 = vertices.get(i);
                p2 = vertices.get((i+1) % vertices.size());
                
                if((p1.getLat() > lat) != (p2.getLat() > lat))
                {
                    double lngIntersect = p1.getLng() + (lng - p1.getLat()) * (p2.getLng() - p1.getLng()) / (p2.getLat() - p1.getLat());

                    if(lng < lngIntersect)
                    {
                        inside = !inside;
                    }
                    
                }
                
            }



            return ResponseEntity.ok(inside);
        } catch(Exception e)
        {
            System.err.println(e);
            return ResponseEntity.badRequest().build();
        }
    }

    
}
