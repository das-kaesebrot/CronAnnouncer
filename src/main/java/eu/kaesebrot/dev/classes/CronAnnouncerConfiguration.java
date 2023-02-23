package eu.kaesebrot.dev.classes;

import java.time.Duration;
import java.util.Map;

public class CronAnnouncerConfiguration {
    private Map<String, ScheduledMessage> scheduledMessageMap;
    private Duration queueAheadDuration;
    private Duration pollingInterval;

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
