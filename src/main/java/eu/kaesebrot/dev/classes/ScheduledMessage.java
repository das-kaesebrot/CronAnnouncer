package eu.kaesebrot.dev.classes;

import com.cronutils.model.Cron;
import eu.kaesebrot.dev.utils.ScheduleConfigParser;

import java.util.Map;

public class ScheduledMessage {
    private MessageType type;
    private String text;
    private Cron schedule;

    public ScheduledMessage(String text, Cron schedule, MessageType type) {
        this.text = text;
        this.schedule = schedule;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public Cron getSchedule() {
        return schedule;
    }
    public MessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("ScheduledMessage(message='%s', schedule='%s', type='%s')", this.text, this.schedule.asString(), this.type.toString().toLowerCase());
    }

    public Map<String, String> asStringMap() {
        return Map.of(
                "message", this.text,
                "schedule", this.getSchedule().asString(),
                "type", type.toString().toLowerCase());
    }
}
