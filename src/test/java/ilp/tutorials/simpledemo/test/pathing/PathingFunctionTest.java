package ilp.tutorials.simpledemo.test.pathing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ilp.tutorials.simpledemo.entity.Cw1.Position;
import ilp.tutorials.simpledemo.entity.Cw2.RestrictedArea;
import ilp.tutorials.simpledemo.pathing.PathingFunctions;
import ilp.tutorials.simpledemo.service.IlpService;
import ilp.tutorials.simpledemo.testData.MedDispatchExampleData;

@SpringBootTest
public class PathingFunctionTest {

    @Autowired
    IlpService ilpService;

    PathingFunctions func = new PathingFunctions();
    MedDispatchExampleData data = new MedDispatchExampleData();

    

    @Test
    void pointIsInRegion_isInRegion()
    {
        List<RestrictedArea> restrictedAreas = ilpService.getRestrictedAreas();

        Position P1 = new Position(-3.1884835952973094, 55.94361880857316);
        for (RestrictedArea area : restrictedAreas) {
            if (area.getName() == "George Square Area")
            {
                assertTrue(func.isInRegion(P1, area.getVertices()));
            }
            
            
        }
        
    }

    @Test
    void pointIsNotInRegion_isInRegion()
    {
        List<RestrictedArea> restrictedAreas = ilpService.getRestrictedAreas();


        Position P1 = new Position(-3.1884835952973094, 55.94361880857316);
        for (RestrictedArea area : restrictedAreas) {
            //System.out.println(func.isInRegion(data.D1, area.getVertices()));
            assertFalse(func.isInRegion(data.D1, area.getVertices()));
            assertFalse(func.isInRegion(data.D2, area.getVertices()));
            assertFalse(func.isInRegion(data.D3, area.getVertices()));
        }
    }

    @Test 
    void pointIsOnBorderOfRegion_isInRegion()
    {

    }
}
