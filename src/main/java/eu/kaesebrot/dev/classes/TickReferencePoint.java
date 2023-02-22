package eu.kaesebrot.dev.classes;

import java.time.ZonedDateTime;

public class TickReferencePoint {
    private long ticks;
    private ZonedDateTime dateTime;

    public TickReferencePoint() {
    }

    public TickReferencePoint(long ticks, ZonedDateTime dateTime) {
        this.ticks = ticks;
        this.dateTime = dateTime;
    }

    public long getTicks() {
        return ticks;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setTicks(long ticks) {
        this.ticks = ticks;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
