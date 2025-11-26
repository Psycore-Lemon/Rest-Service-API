package ilp.tutorials.simpledemo.entity.Cw2;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class AvailabilityRecord {
    
    private String dayOfWeek;
    private LocalTime from;
    private LocalTime until;

    public void setDayOfWeek(String dayOfWeek) {this.dayOfWeek = dayOfWeek;}
    public void setFrom(LocalTime from) {this.from = from;}
    public void setUntil(LocalTime until) {this.until = until;}

    public DayOfWeek getDayOfWeek() {return DayOfWeek.valueOf(dayOfWeek);}
    public LocalTime getFrom() {return from;}
    public LocalTime getUntil() {return until;}

}
