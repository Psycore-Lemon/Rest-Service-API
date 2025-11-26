package ilp.tutorials.simpledemo.entity.Cw2;

public class DispatchReq {

    private double capacity;
    private boolean cooling;
    private boolean heating;
    private double maxCost;

    public void setCapacity(double capacity) {this.capacity = capacity;}
    public void setCooling(boolean cooling) {this.cooling = cooling;}
    public void setHeating(boolean heating) {this.heating = heating;}
    public void setMaxCost(double maxCost) {this.maxCost = maxCost;}

    public double getCapacity() {return capacity;}
    public boolean getCooling() {return cooling;}
    public boolean getHeating() {return heating;}
    public double getMaxCost() {return maxCost;}

}
