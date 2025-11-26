package ilp.tutorials.simpledemo.entity.Cw2;

public class Location {

    private double lng;
    private double lat;
    private int alt;

    public void setLng(double lng) {this.lng = lng;}
    public void setLat(double lat) {this.lat = lat;}
    public void setAlt(int alt) {this.alt = alt;}

    public double getLng() {return lng;}
    public double getLat() {return lat;}
    public int getAlt() {return alt;}
}
