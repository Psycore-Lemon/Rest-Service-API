package ilp.tutorials.simpledemo.entity.Cw2;

public class Capability {

    private boolean cooling;
    private boolean heating;
    private Integer capacity;
    private Integer maxMoves;
    private double costPerMove;
    private double costInitial;
    private double costFinal;

    public void setCooling(boolean cooling) {this.cooling = cooling;}
    public void setHeating(boolean heating) {this.heating = heating;}
    public void setCapacity(Integer capacity) {this.capacity = capacity;}
    public void setMaxMoves(Integer maxMoves) {this.maxMoves = maxMoves;}
    public void setCostPerMove(double costPerMove) {this.costPerMove = costPerMove;}
    public void setCostInitial(double costInitial) {this.costInitial = costInitial;}
    public void setCostFinal(double costFinal) {this.costFinal = costFinal;}

    public boolean getCooling() {return cooling;}
    public boolean getHeating() {return heating;}
    public Integer getCapacity() {return capacity;}
    public Integer getMaxMoves() {return maxMoves;}
    public double getCostPerMove() {return costPerMove;}
    public double getCostInitial() {return costInitial;}
    public double getCostFinal() {return costFinal;}

}
