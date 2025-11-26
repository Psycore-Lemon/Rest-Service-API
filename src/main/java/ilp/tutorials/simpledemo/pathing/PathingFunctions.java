package ilp.tutorials.simpledemo.pathing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ilp.tutorials.simpledemo.entity.Cw1.*;
import ilp.tutorials.simpledemo.entity.Cw2.RestrictedArea;


public class PathingFunctions {
    final double STEP_DISTANCE = 0.000015;
    final double CLOSE_THRESHOLD = 0.00015;



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

    ////////
    /// ///
    
    public String toGeoJson(List<Position> path)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"type\": \"Feature\",\n");
        sb.append("  \"geometry\": {\n");
        sb.append("    \"type\": \"LineString\",\n");
        sb.append("    \"coordinates\": [\n");

        for (int i = 0; i < path.size(); i++) {
            Position p = path.get(i);
            sb.append("      [")
            .append(p.getLng()).append(", ")
            .append(p.getLat()).append("]");

            if (i < path.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("    ]\n");
        sb.append("  },\n");
        sb.append("  \"properties\": {}\n");
        sb.append("}\n");

        return sb.toString();
    }

    public String restrictedAreasToGeoJson(List<RestrictedArea> areas) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n\"type\": \"FeatureCollection\",\n\"features\": [\n");

        for (int i = 0; i < areas.size(); i++) {
            RestrictedArea area = areas.get(i);

            sb.append("  {\n");
            sb.append("    \"type\": \"Feature\",\n");
            sb.append("    \"properties\": {\n");
            sb.append("      \"name\": \"").append(area.getName()).append("\",\n");
            sb.append("      \"id\": ").append(area.getId()).append("\n");
            sb.append("    },\n");
            sb.append("    \"geometry\": {\n");
            sb.append("      \"type\": \"Polygon\",\n");
            sb.append("      \"coordinates\": [ [\n");

            List<Position> vertices = area.getVertices();

            // Ensure polygon is closed
            for (int j = 0; j < vertices.size(); j++) {
                Position p = vertices.get(j);
                sb.append("        [")
                .append(p.getLng()).append(", ")
                .append(p.getLat()).append("]");
                if (j < vertices.size() - 1) sb.append(",");
                sb.append("\n");
            }

            // Repeat first vertex at end to close polygon
            Position first = vertices.get(0);
            sb.append("        [")
            .append(first.getLng()).append(", ")
            .append(first.getLat()).append("]\n");

            sb.append("      ] ]\n"); // closes coordinates
            sb.append("    }\n");   // closes geometry
            sb.append("  }");

            if (i < areas.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("]}\n");
        return sb.toString();
    }

    public String toCombinedGeoJson(List<List<Position>> paths, List<RestrictedArea> areas)   
    {
        StringBuilder sb = new StringBuilder();

        sb.append("{\n");
        sb.append("  \"type\": \"FeatureCollection\",\n");
        sb.append("  \"features\": [\n");

        boolean first = true;

        // ============================
        // 1. Add each PATH as LineString
        // ============================
        for (List<Position> path : paths) {
            if (!first) sb.append(",\n");
            first = false;

            sb.append("    {\n");
            sb.append("      \"type\": \"Feature\",\n");
            sb.append("      \"geometry\": {\n");
            sb.append("        \"type\": \"LineString\",\n");
            sb.append("        \"coordinates\": [\n");

            for (int i = 0; i < path.size(); i++) {
                Position p = path.get(i);
                sb.append(String.format("          [%.10f, %.10f]", p.getLng(), p.getLat()));
                if (i < path.size() - 1) sb.append(",");
                sb.append("\n");
            }

            sb.append("        ]\n");
            sb.append("      },\n");
            sb.append("      \"properties\": {\n");
            sb.append("        \"stroke\": \"#ff0000\",\n");
            sb.append("        \"stroke-width\": 2\n");
            sb.append("      }\n");
            sb.append("    }");
        }

        // ============================
        // 2. Add each RESTRICTED AREA as Polygon
        // ============================
        for (RestrictedArea area : areas) {
            List<Position> verts = area.getVertices();

            sb.append(",\n");
            sb.append("    {\n");
            sb.append("      \"type\": \"Feature\",\n");
            sb.append("      \"geometry\": {\n");
            sb.append("        \"type\": \"Polygon\",\n");
            sb.append("        \"coordinates\": [[\n");

            for (int i = 0; i < verts.size(); i++) {
                Position p = verts.get(i);
                sb.append(String.format("          [%.10f, %.10f]", p.getLng(), p.getLat()));
                if (i < verts.size() - 1) sb.append(",");
                sb.append("\n");
            }

            sb.append("        ]]\n");
            sb.append("      },\n");
            sb.append("      \"properties\": {\n");
            sb.append("        \"name\": \"" + area.getName() + "\",\n");
            sb.append("        \"fill\": \"#0000ff\",\n");
            sb.append("        \"fill-opacity\": 0.3,\n");
            sb.append("        \"stroke\": \"#0000ff\"\n");
            sb.append("      }\n");
            sb.append("    }");
        }

        sb.append("\n  ]\n");
        sb.append("}");

        return sb.toString();
    }



    public void saveGeoJsonToFile(String geoJson, String filename) {
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(geoJson);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save GeoJSON", e);
        }
    }
}
