package ilp.tutorials.simpledemo.entity.Cw2;

public class Drone {

        private String name;
        private Integer id;
        private Capability capability;

        public void setName(String name) {this.name = name;}
        public void setId(Integer id) {this.id = id;}
        public void setCapability(Capability capability) {this.capability = capability;}

        public String getName() {return name;}
        public Integer getId() {return id;}
        public Capability getCapability() {return capability;}
}
