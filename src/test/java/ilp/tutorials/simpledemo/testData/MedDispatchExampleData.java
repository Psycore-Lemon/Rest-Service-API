package ilp.tutorials.simpledemo.testData;

import java.time.LocalDate;
import java.util.List;

import ilp.tutorials.simpledemo.entity.Cw1.Position;
import ilp.tutorials.simpledemo.entity.Cw2.DispatchReq;
import ilp.tutorials.simpledemo.entity.Cw2.MedDispatchRec;

public class MedDispatchExampleData {

    public Position D1 = new Position(-3.1736907481424077, 55.939040450833346); // Commonwealth Pool
    public Position D2 = new Position( -3.200426038220301, 55.94869903260735); // Edinburgh Castle
    public Position D3 = new Position(-3.1669410510413925, 55.97655250521657); //Leith

    

    MedDispatchRec d1 = buildDispatch(1, 13.5, 0.75, D1);
    MedDispatchRec d2 = buildDispatch(1, 13.5, 0.75, D2);
    MedDispatchRec d3 = buildDispatch(1, 13.5, 0.75, D3);

    List<MedDispatchRec> testData = List.of(d1,d2,d3);

    public List<MedDispatchRec> getData() {return testData;}

    private MedDispatchRec buildDispatch(int id,double maxCost, double capacity, Position pos)
    {
        MedDispatchRec rec = new MedDispatchRec();
        rec.setId(id);
        rec.setDate("2025-12-22");
        rec.setTime("14:30");

        DispatchReq req = new DispatchReq();
        req.setCapacity(capacity);
        req.setCooling(false);
        req.setHeating(false);
        req.setMaxCost(maxCost);

        rec.setDispatchReq(req);
        rec.setDelivery(pos);

        return rec;

    }
}


