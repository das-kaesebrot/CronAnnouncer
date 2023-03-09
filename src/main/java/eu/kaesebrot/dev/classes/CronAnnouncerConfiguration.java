package eu.kaesebrot.dev.classes;

import java.time.Duration;
import java.util.Map;

public class CronAnnouncerConfiguration {
    private final Map<String, ScheduledMessage> scheduledMessageMap;
    private final Duration queueAheadDuration;
    private final Duration pollingInterval;

    public CronAnnouncerConfiguration(Map<String, ScheduledMessage> scheduledMessageMap, Duration queueAheadDuration, Duration pollingInterval) {
        this.scheduledMessageMap = scheduledMessageMap;
        this.queueAheadDuration = queueAheadDuration;
        this.pollingInterval = pollingInterval;
    }

    public Map<String, ScheduledMessage> getScheduledMessageMap() {
        return scheduledMessageMap;
    }

    public Duration getQueueAheadDuration() {
        return queueAheadDuration;
    }

    public Duration getPollingInterval() {
        return pollingInterval;
    }
}
