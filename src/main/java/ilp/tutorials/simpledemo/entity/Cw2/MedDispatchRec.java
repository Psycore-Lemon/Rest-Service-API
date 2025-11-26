package ilp.tutorials.simpledemo.entity.Cw2;

import java.time.LocalDate;
import java.time.LocalTime;

import ilp.tutorials.simpledemo.entity.Cw1.Position;

public class MedDispatchRec {

    private int id;
    private LocalDate date;
    private LocalTime time;
    private DispatchReq dispatchReq;
    private Position delivery;

    public void setId(int id) {this.id = id;}
    public void setDate(String date) {this.date = LocalDate.parse(date);}
    public void setTime(String time) {this.time = LocalTime.parse(time);}
    public void setDispatchReq(DispatchReq dispatchReq) {this.dispatchReq = dispatchReq;}
    public void setDelivery(Position delivery) {this.delivery = delivery;}

    public int getId() {return id;}
    public LocalDate getDate() {return date;}
    public LocalTime getTime() {return time;}
    public DispatchReq getDispatchReq() {return dispatchReq;}
    public Position getDelivery() {return delivery;}
}
