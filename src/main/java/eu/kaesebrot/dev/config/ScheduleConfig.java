package eu.kaesebrot.dev.config;

import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import eu.kaesebrot.dev.CronAnnouncerPlugin;
import eu.kaesebrot.dev.classes.ScheduledMessage;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.plugin.PluginLogger;

import java.util.HashMap;
import java.util.Map;

import static com.cronutils.model.CronType.CRON4J;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

@SerializableAs("schedules")
public class ScheduleConfig implements ConfigurationSerializable
{
    {
        ConfigurationSerialization.registerClass(ScheduleConfig.class, "schedules");
    }
    private String KEY_MESSAGE = "message";
    private String KEY_SCHEDULE = "schedule";
    private Map<String, ScheduledMessage> scheduledMessages;
    private PluginLogger logger = new PluginLogger(getPlugin(CronAnnouncerPlugin.class));
    private CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CRON4J));

    public ScheduleConfig(Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if(entry.getValue() instanceof Map)
            {
                try {
                    scheduledMessages.put(entry.getKey(), parseEntry((Map<String, String>) entry.getValue()));
                } catch (IllegalArgumentException exception) {
                    logger.warning(String.format("Unable to parse cron expression for key %s: '%s'", entry.getKey(), entry.getValue()));
                    logger.warning(exception.getMessage());
                }
            }
        }
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serializedMap = new HashMap<>();

        for (var entry : scheduledMessages.entrySet()) {
            serializedMap.put(entry.getKey(), entry.getValue().asStringMap());
        }

        return serializedMap;
    }

    public Map<String, ScheduledMessage> getSchedules() {
        return this.scheduledMessages;
    }

    private ScheduledMessage parseEntry(Map<String, String> scheduleEntry) {
        if (!(scheduleEntry.containsKey(KEY_MESSAGE) && scheduleEntry.containsKey(KEY_SCHEDULE))) {
            throw new IllegalArgumentException("Invalid schedule entry");
        }

        var parsedCronValue = parser.parse(scheduleEntry.get(KEY_SCHEDULE));

        return new ScheduledMessage(parsedCronValue, scheduleEntry.get(KEY_MESSAGE));
    }
}
