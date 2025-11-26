package ilp.tutorials.simpledemo.entity.Cw2;

import java.util.List;

public class DronesForServicePoint {

    private int servicePointId;
    private List<ServicePointDrone> drones;

    public void setServicePointId(int servicePointId) {this.servicePointId = servicePointId;}
    public void setDrones(List<ServicePointDrone> drones) {this.drones = drones;}

    public int getServicePointId() {return servicePointId;}
    public List<ServicePointDrone> getDrones() {return drones;}

}
