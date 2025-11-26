package ilp.tutorials.simpledemo.entity.Cw2.Delivery;

import java.util.List;

import ilp.tutorials.simpledemo.entity.Cw1.Position;

public class DeliveryPath {

    private int deliveryId;
    private List<Position> flightPath;

    public DeliveryPath(int deliveryId, List<Position> flightPath)
    {
        this.deliveryId = deliveryId;
        this.flightPath = flightPath;
    }

    public int getDeliveryId() {return deliveryId;}
    public List<Position> getFlightPath() {return flightPath;}
}
