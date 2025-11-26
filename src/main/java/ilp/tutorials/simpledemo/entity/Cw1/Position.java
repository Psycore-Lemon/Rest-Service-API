package ilp.tutorials.simpledemo.entity.Cw1;

public class Position {
    private final int ROUND_PLACE = 6;
    private double lng;
    private double lat;

    public Position(double lng, double lat)
    {
        this.lng = roundToDecimal(lng, ROUND_PLACE);
        this.lat = roundToDecimal(lat, ROUND_PLACE);
    }

    public void setLng(double lng) 
    {
        this.lng = roundToDecimal(lng, ROUND_PLACE);
    }
    public void setLat(double lat) 
    {
        this.lat = roundToDecimal(lat, ROUND_PLACE);
    }

    public double getLng() {return lng;}
    public double getLat() {return lat;}

    public boolean equals(Position targetPos) {
        
        if (this.lng == targetPos.getLng() && this.lat == targetPos.getLat())
        {
            return true;
        }

        return false;
    }

    private double roundToDecimal(double value, int places)
    {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) /scale;
    }
}
