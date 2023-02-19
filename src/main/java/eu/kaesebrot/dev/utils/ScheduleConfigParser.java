package eu.kaesebrot.dev.utils;

import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import eu.kaesebrot.dev.classes.MessageType;
import eu.kaesebrot.dev.classes.ScheduledMessage;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

import static com.cronutils.model.CronType.CRON4J;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class ScheduleConfigParser
{
    private String KEY_ROOT = "schedules";
    private String KEY_MESSAGE = "message";
    private String KEY_SCHEDULE = "schedule";
    private String KEY_TYPE = "type";
    private final CronParser parser;

    private JavaPlugin plugin;

    public ScheduleConfigParser(JavaPlugin plugin) {
        this.plugin = plugin;
        this.parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CRON4J));
    }

    public Map<String, ScheduledMessage> parseConfig()
    {
        Map<String, ScheduledMessage> parsedMessages = new HashMap<>();

        // do nothing if root is empty
        if (!this.plugin.getConfig().contains(KEY_ROOT)) return parsedMessages;

        var subKeys = this.plugin.getConfig().getConfigurationSection(KEY_ROOT).getValues(false);

        for (Map.Entry<String, Object> entry : subKeys.entrySet()) {
            if(entry.getValue() instanceof MemorySection)
            {
                try {
                    var result = parseEntry((MemorySection) entry.getValue());
                    parsedMessages.put(entry.getKey(), result);
                    plugin.getLogger().info(String.format("Successfully parsed message '%s' with schedule '%s'", entry.getKey(), result.getSchedule().asString()));
                } catch (IllegalArgumentException exception) {
                    plugin.getLogger().warning(String.format("Unable to parse cron expression for key %s: '%s'", entry.getKey(), entry.getValue()));
                    plugin.getLogger().warning(exception.getMessage());
                }
            } else {
                plugin.getLogger().warning(String.format("Entry '%s' not a map, ignoring it. Value: %s", entry.getKey(), entry.getValue().toString()));
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
