package ilp.tutorials.simpledemo.performanceTest;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ilp.tutorials.simpledemo.entity.Cw2.MedDispatchRec;
import ilp.tutorials.simpledemo.service.DeliveryService;
import ilp.tutorials.simpledemo.service.IlpService;
import ilp.tutorials.simpledemo.testData.MedDispatchExampleData;

@SpringBootTest
public class CalcDeliveryPathPerformanceTest {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private IlpService ilpService;

    MedDispatchExampleData test = new MedDispatchExampleData();

    @Test
    void performanceTest_calcDeliveryPath()
    {
        List<MedDispatchRec> recs = test.getData();

        int runs = 10;
        long totalTime = 0;
        System.out.println("===============================================");
        System.out.println("============/// START THE TEST /// ============");
        System.out.println("===============================================");
        for (int i = 0; i < runs; i ++)
        {
            long start = System.nanoTime();

            var result = deliveryService.calcDeliveryPath(recs);

            long end = System.nanoTime();
            long durationMs = (end - start) / 1_000_000L;

            totalTime += durationMs;

            System.out.println("Run " + i + " took " + durationMs + "ms");

        }

        long avg = totalTime / runs;
        System.out.println("===============================================");
        System.out.println("Average time for calcDeliveryPath = " +avg +"ms");
    }

}
