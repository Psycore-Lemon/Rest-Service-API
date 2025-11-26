package ilp.tutorials.simpledemo.test.pathing;

import ilp.tutorials.simpledemo.entity.Cw1.Position;
import ilp.tutorials.simpledemo.entity.Cw2.Location;
import ilp.tutorials.simpledemo.entity.Cw2.MedDispatchRec;
import ilp.tutorials.simpledemo.entity.Cw2.RestrictedArea;
import ilp.tutorials.simpledemo.pathing.AStarPathFinder;
import ilp.tutorials.simpledemo.pathing.PathingFunctions;
import ilp.tutorials.simpledemo.service.DroneService;
import ilp.tutorials.simpledemo.service.IlpService;
import ilp.tutorials.simpledemo.testData.MedDispatchExampleData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.Key;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AStarPathFinderTest {

    @Autowired
    private IlpService ilpService;

    @Autowired
    private DroneService droneService;

    private MedDispatchExampleData data = new MedDispatchExampleData();
    

    @Test
    void straightLinePath_Works()
    {
        AStarPathFinder pf = new AStarPathFinder(List.of());
        Position start = new Position(-3.18, 55.94);
        Position goal = new Position(-3.19, 55.94);

        List<Position> path = pf.findPath(start, goal);

        assertNotNull(path);
        assertTrue(path.size() > 1); // has moved
        for( Position point : path)
        {
            assertEquals(point.getLat(), 55.94, 1e-9);
        
        }
        assertEquals(goal.getLng(), path.get(path.size() - 1).getLng(), 1e-9);
        assertEquals(goal.getLat(), path.get(path.size() - 1).getLat(), 1e-9);

    }

    @Test
    void noRestrictedArea_NoRerouting()
    {
        AStarPathFinder pf = new AStarPathFinder(List.of());
        Position start = new Position(-3.18, 55.94);
        Position goal = new Position(-3.18000014, 55.94000014);

        List<Position> path = pf.findPath(start, goal);

        assertNotNull(path);
        //assertEquals(start, path.get(1));
    }

    @Test 
    void restrictedArea_ForcesDetour()
    {
        RestrictedArea square = new RestrictedArea();
        square.setId(1);

        square.setVertices(List.of(
            new Position(0.00000060, -0.00000060),
            new Position(0.00000060, 0.00000060),
            new Position(-0.00000060, 0.00000060),
            new Position(-0.00000060, -0.00000060),
            new Position(0.00000060, -0.00000060)

        ));

        List<RestrictedArea> areas = List.of(square);

        AStarPathFinder pf = new AStarPathFinder(areas);
        PathingFunctions pathing = new PathingFunctions();

        Position start = new Position(-0.0018, 0);
        Position goal = new Position(0.0018, 0);

        List<Position> path = pf.findPath(start, goal);

        List<List<Position>> list = List.of(path);

        pathing.saveGeoJsonToFile(pathing.toCombinedGeoJson(list, areas), "testMap");

        assertNotNull(path);
        //System.err.println(path.size());
        System.err.println(pathing.isInRegion(new Position(0, 0), square.getVertices()));
        for( Position point : path)
        {
           //System.err.println(pathing.isInRegion(point, square.getVertices()));
            assertFalse(pathing.isInRegion(point, square.getVertices()));
        
        }

        assertTrue(path.size() > 1);

    }

    @Test
    void performanceTest_AStarPathFinder()
    {
        System.out.println("===============================================");
        System.out.println("============/// START THE TEST /// ============");
        System.out.println("===============================================");

        List<MedDispatchRec> recs = data.getData();

        Position D1 = data.D1; // Commonwealth Pool
        Position D2 = new Position( -3.20042603, 55.94869903); // Edinburgh Castle
        Position D3 = new Position(-3.16694105, 55.97655250); //Leith

        System.out.println(D1.getLng() + " , " + D1.getLat());
        System.out.println(D2.getLng() + " , " + D2.getLat());
        System.out.println(D3.getLng() + " , " + D3.getLat());

        Location sp = ilpService.getServicePoints().get(0).getLocation();
        Position home = new Position(sp.getLng(), sp.getLat());

        int runs = 1;
        long totalTime = 0;

        List<RestrictedArea> areas = ilpService.getRestrictedAreas();
        AStarPathFinder pf = new AStarPathFinder(areas);

        for (int i = 0; i < runs; i ++)
        {
            long start = System.nanoTime();

            var result = pf.findPath(home, D2);

            long end = System.nanoTime();
            long durationMs = (end - start) / 1_000_000L;

            totalTime += durationMs;

            System.out.println("Run " + i + " took " + durationMs + "ms");

        }

        long avg = totalTime / runs;
        System.out.println("===============================================");
        System.out.println("Average time for calcDeliveryPath = " +avg +"ms");



    }

    @Test
    void keyTest()
    {
        Position D2 = new Position( -3.200426038220301, 55.94869903260735);
        Position DD2 = new Position( -3.2004260382, 55.9486990326);

        HashMap<String, Position> test = new HashMap<>();

        System.out.println(data.D1.getLng() + " , " + data.D1.getLat());
        System.out.println(key(data.D1));

        test.put(key(D2), D2);
        assertEquals(key(D2),key(DD2));
        assertEquals(D2,test.get(key(DD2)));
    }

    private String key(Position p)
        {
            return String.format("%.8f_%.8f", p.getLng(), p.getLat());
        }


    
}
