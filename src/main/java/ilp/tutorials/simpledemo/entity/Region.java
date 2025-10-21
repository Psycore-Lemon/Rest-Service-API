package ilp.tutorials.simpledemo.entity;

import java.util.List;

public class Region {
    private String name;
    private List<Position> vertices;
    
    public String getName() {return name;}
    public List<Position> getVertices() {return vertices;}

    public void setName(String name) {this.name = name;}
    public void setVertices(List<Position> vertices) {this.vertices = vertices;}
}
