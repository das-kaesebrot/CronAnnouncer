package eu.kaesebrot.dev.utils;

import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import eu.kaesebrot.dev.CronAnnouncerPlugin;
import eu.kaesebrot.dev.classes.ScheduledMessage;
import org.bukkit.plugin.PluginLogger;

import java.util.HashMap;
import java.util.Map;

import static com.cronutils.model.CronType.CRON4J;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class ScheduleConfigParser
{
    private String KEY_MESSAGE = "message";
    private String KEY_SCHEDULE = "schedule";
    private PluginLogger logger = new PluginLogger(getPlugin(CronAnnouncerPlugin.class));
    private CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CRON4J));

    public Map<String, ScheduledMessage> parseConfigMap(Map<String, Object> values)
    {
        Map<String, ScheduledMessage> parsedMessages = new HashMap<>();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if(entry.getValue() instanceof Map)
            {
                try {
                    parsedMessages.put(entry.getKey(), parseEntry((Map<String, String>) entry.getValue()));
                } catch (IllegalArgumentException exception) {
                    logger.warning(String.format("Unable to parse cron expression for key %s: '%s'", entry.getKey(), entry.getValue()));
                    logger.warning(exception.getMessage());
                }
            }
        }

        return parsedMessages;
    }

    private ScheduledMessage parseEntry(Map<String, String> scheduleEntry) {
        if (!(scheduleEntry.containsKey(KEY_MESSAGE) && scheduleEntry.containsKey(KEY_SCHEDULE))) {
            throw new IllegalArgumentException("Invalid schedule entry");
        }

        var parsedCronValue = parser.parse(scheduleEntry.get(KEY_SCHEDULE));

        return new ScheduledMessage(parsedCronValue, scheduleEntry.get(KEY_MESSAGE).replaceAll("(&([a-f0-9]))", "\u00A7$2"));
    }
}
