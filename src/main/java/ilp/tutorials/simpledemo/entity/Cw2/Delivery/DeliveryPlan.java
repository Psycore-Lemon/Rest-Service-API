package ilp.tutorials.simpledemo.entity.Cw2.Delivery;

import java.util.List;

public class DeliveryPlan {

    private double totalCost;
    private int totalMoves;
    private List<DronePath> dronePaths;

    public DeliveryPlan(double totalCost, int totalMoves, List<DronePath> dronePaths)
    {
        this.totalCost = totalCost;
        this.totalMoves = totalMoves;
        this.dronePaths = dronePaths;
    }

    public double getTotalCost() {return totalCost;}
    public int getTotalMoves() {return totalMoves;}
    public List<DronePath> getDronePaths() {return dronePaths;}
}
