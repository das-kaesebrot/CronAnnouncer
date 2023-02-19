package eu.kaesebrot.dev;

import eu.kaesebrot.dev.classes.ScheduledMessage;
import eu.kaesebrot.dev.tasks.ScheduledMessageTaskQueuer;
import eu.kaesebrot.dev.utils.ScheduleConfigParser;
import eu.kaesebrot.dev.utils.TickConverter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.logging.Logger;

public class CronAnnouncerPlugin extends JavaPlugin {
    private final ScheduleConfigParser scheduleConfigParser = new ScheduleConfigParser();
    private final TickConverter tickConverter = new TickConverter();
    private FileConfiguration config;
    private Logger logger;
    private BukkitTask subtaskScheduler;

    Map<String, ScheduledMessage> scheduledMessages;

    public CronAnnouncerPlugin() {
        config = getConfig();
        logger = getLogger();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        var scheduledMessagesMap = this.getConfig().getConfigurationSection("schedules").getValues(true);
        scheduledMessages = scheduleConfigParser.parseConfigMap(scheduledMessagesMap);

        cancelAllTasks();
        queueInitialScheduler();
    }

    @Override
    public void onDisable() {
        cancelAllTasks();
        logger.info(getName() + " has been disabled!");
    }

    private void queueInitialScheduler() {
        var queueAheadDuration = Duration.of(1, ChronoUnit.HOURS);
        var queuer = new ScheduledMessageTaskQueuer(this, scheduledMessages, queueAheadDuration);

        subtaskScheduler = queuer.runTaskTimer(this, 0L, tickConverter.durationToTicks(queueAheadDuration));
    }

    private void cancelAllTasks() {
        getServer().getScheduler().cancelTasks(this);
    }
}
