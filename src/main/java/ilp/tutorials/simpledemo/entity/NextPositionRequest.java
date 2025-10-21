package ilp.tutorials.simpledemo.entity;

public class NextPositionRequest {
    private Position start;
    private double angle;

    public Position getStart() {return start;}
    public double getAngle() {return angle;}

    public void setStart(Position start) {this.start = start;}
    public void setAngle(double angle) {this.angle = angle;}

}
