package ilp.tutorials.simpledemo.entity.Cw2;

import java.util.List;

import ilp.tutorials.simpledemo.entity.Cw1.Position;

public class RestrictedArea {

    private String name;
    private int id;
    private Limits limits;
    private List<Position> vertices;

    public void setName(String name) {this.name = name;}
    public void setId(int id) {this.id = id;}
    public void setLimits(Limits limits) {this.limits = limits;}
    public void setVertices(List<Position> vertices) {this.vertices = vertices;}

    public String getName() {return name;}
    public int getId() {return id;}
    public Limits getLimits() {return limits;}
    public List<Position> getVertices() {return vertices;}

}
