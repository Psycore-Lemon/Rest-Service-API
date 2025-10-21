package ilp.tutorials.simpledemo.entity;

public class Position {
    private double lng;
    private double lat;

    public Position(double lng, double lat)
    {
        this.lng = lng;
        this.lat = lat;
    }

    public void setLng(double lng) {this.lng = lng;}
    public void setLat(double lat) {this.lat = lat;}

    public double getLng() {return lng;}
    public double getLat() {return lat;}

    public boolean equals(Position targetPos) {
        
        if (this.lng == targetPos.getLng() && this.lat == targetPos.getLat())
        {
            return true;
        }

        return false;
    }
}
