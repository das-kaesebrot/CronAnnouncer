package eu.kaesebrot.dev.classes;

import com.cronutils.model.Cron;

import java.util.Map;

public class ScheduledMessage {
    private Cron schedule;
    private String message;

    public ScheduledMessage(Cron schedule, String message) {
        this.schedule = schedule;
        this.message = message;
    }

    public Cron getSchedule() {
        return schedule;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> asStringMap() {
        return Map.of("message", this.message, "schedule", this.getSchedule().asString());
    }
}
