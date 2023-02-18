package eu.kaesebrot.dev.classes;

import com.cronutils.model.Cron;

import java.util.Map;

public class ScheduledMessage {
    private Cron schedule;
    private String text;

    public ScheduledMessage(Cron schedule, String text) {
        this.schedule = schedule;
        this.text = text;
    }

    public Cron getSchedule() {
        return schedule;
    }

    public String getText() {
        return text;
    }

    public Map<String, String> asStringMap() {
        return Map.of("message", this.text, "schedule", this.getSchedule().asString());
    }
}
