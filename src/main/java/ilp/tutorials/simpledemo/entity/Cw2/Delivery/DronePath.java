package ilp.tutorials.simpledemo.entity.Cw2.Delivery;

import java.util.List;

import ilp.tutorials.simpledemo.entity.Cw2.Capability;
import ilp.tutorials.simpledemo.entity.Cw2.Drone;

public class DronePath {

    private int id;
    private List<DeliveryPath> deliveries;

    public DronePath(int id, List<DeliveryPath> deliveries)
    {
        this.id = id;
        this.deliveries = deliveries;
    }

    public int getId() {return id;}
    public List<DeliveryPath> getDeliveries() {return deliveries;}

    public int countMoves()
    {
        return deliveries.stream()
                .mapToInt(dp -> dp.getFlightPath().size()-1)
                .sum();
    }

    public double computeCost(Drone drone, int moves)
    {
        Capability cap = drone.getCapability();
        return cap.getCostInitial() + cap.getCostFinal() + (cap.getCostPerMove() * moves);
    }
    
}
