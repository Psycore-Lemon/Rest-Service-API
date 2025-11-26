package ilp.tutorials.simpledemo.entity.Cw2;

import java.util.List;

public class ServicePointDrone {
    
    private int id;
    private List<AvailabilityRecord> availability;
    
    public void setId(int id) {this.id = id;}
    public void setAvailability(List<AvailabilityRecord> availability) {this.availability = availability;}

    public int getId() {return id;}
    public List<AvailabilityRecord> getGetAvailability() {return availability;}

}
