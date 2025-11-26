package ilp.tutorials.simpledemo.entity.Cw2;

public class QueryAttributes {
    private String attribute;
    private String operator;
    private String value;

    public void setAttribute(String attribute) {this.attribute = attribute;}
    public void setOperator(String operator) {this.operator = operator;}
    public void setValue(String value) {this.value = value;}

    public String getAttribute() {return attribute;}
    public String getOperator() {return operator;}
    public String getValue() {return value;}

}
