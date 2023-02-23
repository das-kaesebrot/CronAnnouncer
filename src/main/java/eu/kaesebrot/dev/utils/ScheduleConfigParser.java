package eu.kaesebrot.dev.utils;

import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import eu.kaesebrot.dev.classes.CronAnnouncerConfiguration;
import eu.kaesebrot.dev.classes.MessageType;
import eu.kaesebrot.dev.classes.ScheduledMessage;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static com.cronutils.model.CronType.CRON4J;

public class ScheduleConfigParser
{
    private String KEY_ROOT = "schedules";
    private String KEY_MESSAGE = "message";
    private String KEY_SCHEDULE = "schedule";

    private String KEY_QUEUE_DURATION = "queue_duration";
    private String KEY_POLLING_INTERVAL = "polling_interval";
    private String KEY_TYPE = "type";
    private final CronParser parser;

    private final JavaPlugin plugin;

    public ScheduleConfigParser(JavaPlugin plugin) {
        this.plugin = plugin;
        this.parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CRON4J));
    }


    public CronAnnouncerConfiguration parseConfig()
    {
        Duration queueAheadDuration = this.plugin.getConfig().contains(KEY_QUEUE_DURATION) ?
                Duration.parse(this.plugin.getConfig().getString(KEY_QUEUE_DURATION)) : Duration.of(1, ChronoUnit.HOURS);

        Duration pollingInterval = this.plugin.getConfig().contains(KEY_POLLING_INTERVAL) ?
                Duration.parse(this.plugin.getConfig().getString(KEY_POLLING_INTERVAL)) : Duration.of(10, ChronoUnit.SECONDS);

        return new CronAnnouncerConfiguration(parseMessages(), queueAheadDuration, pollingInterval);
    }

    private Map<String, ScheduledMessage> parseMessages()
    {
        Map<String, ScheduledMessage> parsedMessages = new HashMap<>();

        // do nothing if root is empty
        if (!this.plugin.getConfig().contains(KEY_ROOT)) return parsedMessages;

        //noinspection DataFlowIssue
        var subKeys = this.plugin.getConfig().getConfigurationSection(KEY_ROOT).getValues(false);

        for (Map.Entry<String, Object> entry : subKeys.entrySet()) {
            if(entry.getValue() instanceof MemorySection)
            {
                try {
                    var result = parseEntry((MemorySection) entry.getValue());
                    parsedMessages.put(entry.getKey(), result);
                    plugin.getLogger().info(String.format("Successfully parsed message %s=%s", entry.getKey(), result));
                } catch (IllegalArgumentException exception) {
                    plugin.getLogger().warning(String.format("Unable to parse entry for key %s: '%s'", entry.getKey(), entry.getValue()));
                    plugin.getLogger().warning(exception.getMessage());
                }
            } else {
                plugin.getLogger().warning(String.format("Entry '%s' not a MemorySection, ignoring it. Value: %s", entry.getKey(), entry.getValue().toString()));
            }
        }

        return parsedMessages;
    }

    private ScheduledMessage parseEntry(MemorySection scheduleEntry) {
        if (!(
                scheduleEntry.contains(KEY_MESSAGE)
                && scheduleEntry.contains(KEY_SCHEDULE)
                && scheduleEntry.contains(KEY_TYPE))
        ) {
            throw new IllegalArgumentException("Invalid schedule entry");
        }

        var parsedCronValue = parser.parse(scheduleEntry.getString(KEY_SCHEDULE));
        var parsedText = scheduleEntry.getString(KEY_MESSAGE).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        var parsedType = MessageType.valueOf(scheduleEntry.getString(KEY_TYPE).toUpperCase());

        return new ScheduledMessage(parsedText, parsedCronValue, parsedType);
    }
}
