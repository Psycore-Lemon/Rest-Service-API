package ilp.tutorials.simpledemo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ilp.tutorials.simpledemo.entity.Cw1.*;

@Service
public class PathingService {
    final double STEP_DISTANCE = 0.000015;
    final double CLOSE_THRESHOLD = 0.00015;

    @Autowired
    private IlpService ilpService;

    @Autowired
    private DroneService droneService;

    public Double distanceTo(Position point1, Position point2) {
        try
        {
            double lng1 = point1.getLng();
            double lat1 = point1.getLat();
            double lng2 = point2.getLng();
            double lat2 = point2.getLat();

            double distance = Math.sqrt(Math.pow(lng2-lng1, 2) + Math.pow(lat2-lat1,2));

            return distance;

        } catch(Exception e)
        {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public Boolean isCloseTo (Position point1, Position point2) {
        try
        {
            double lng1 = point1.getLng();
            double lat1 = point1.getLat();
            double lng2 = point2.getLng();
            double lat2 = point2.getLat();

            double distance = Math.sqrt(Math.pow(lng2-lng1, 2) + Math.pow(lat2-lat1,2));
            Boolean isClose = (distance<CLOSE_THRESHOLD) ? true : false;

            return isClose;

        } catch(Exception e)
        {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public Position nextPosition(Position start, double angleRad) {
        try
        {            
            double nextLng = start.getLng() + (STEP_DISTANCE * Math.cos(angleRad));
            double nextLat = start.getLat() + (STEP_DISTANCE * Math.sin(angleRad));

            Position nexPosition = new Position(nextLng, nextLat);

            return nexPosition;
        } catch(Exception e)
        {
            return null;
        }
    }

    public Boolean isInRegion(Position point, List<Position> vertices) {
        try
        {
            double lng = point.getLng();
            double lat = point.getLat();

            //List<Position> vertices = region.getVertices();
            
            boolean inside = false;

            if (vertices.size() < 4 || !(vertices.get(0).equals(vertices.get(vertices.size()-1))))
            {
                return null;
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



            return inside;
        } catch(Exception e)
        {
            System.err.println(e);
            return null;
        }
    }

    public boolean segmentIntersectsRegion(Position a, Position b, List<Position> region)
    {
        int n = region.size();

        if (isInRegion(a, region) && isInRegion(b, region))
        {
            return false;
        }

        for (int i = 0; i < n - 1; i++)
        {
            Position p1 = region.get(i);
            Position p2 = region.get(i+1);
            if(segmentIntersect(a, b, p1, p2))
            {
                return true;
            }   
        }

        Position pLast = region.get(n-1);
        Position pFirst = region.get(0);
        return segmentIntersect(a, b, pLast, pFirst);
    }

    private boolean segmentIntersect(Position s1Start, Position s1End, Position s2Start, Position s2End)
    {
        return isCounterClockwise(s1Start, s2Start, s2End) != isCounterClockwise(s1End, s2Start, s2End) &&
                isCounterClockwise(s1Start, s1End, s2Start) != isCounterClockwise(s1Start, s1End, s2End);
    }

    private boolean isCounterClockwise(Position start, Position mid, Position end)
    {
        double area = (end.getLat() - start.getLat()) * (mid.getLat() - start.getLng()) -
                      (mid.getLat() - start.getLat()) * (end.getLng() - start.getLng());

        return area > 0;
    }

}
