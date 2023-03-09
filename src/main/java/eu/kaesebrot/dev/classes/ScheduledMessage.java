package eu.kaesebrot.dev.classes;

import com.cronutils.model.Cron;

import java.util.Map;

public record ScheduledMessage(String text, Cron schedule, MessageType type) {

    @Override
    public String toString() {
        return String.format("ScheduledMessage(message='%s', schedule='%s', type='%s')", this.text, this.schedule.asString(), this.type.toString().toLowerCase());
    }

    public String toStringInCommand() {
        return String.format("message='%s\\u00A7r', schedule='%s', type='%s'", this.text, this.schedule.asString(), this.type.toString().toLowerCase());
    }

    public Map<String, String> asStringMap() {
        return Map.of(
                "message", this.text,
                "schedule", this.schedule().asString(),
                "type", type.toString().toLowerCase());
    }
}
