package eu.kaesebrot.dev;

import eu.kaesebrot.dev.classes.CronAnnouncerConfiguration;
import eu.kaesebrot.dev.classes.ScheduledMessage;
import eu.kaesebrot.dev.tasks.ScheduledMessageTaskScheduler;
import eu.kaesebrot.dev.utils.ScheduleConfigParser;
import eu.kaesebrot.dev.utils.TickConverter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class CronAnnouncerPlugin extends JavaPlugin {
    private ScheduleConfigParser scheduleConfigParser;
    private final TickConverter tickConverter = new TickConverter();
    private BukkitTask subtaskSchedulerTask;
    private CronAnnouncerConfiguration configuration;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        scheduleConfigParser = new ScheduleConfigParser(this);
        configuration = scheduleConfigParser.parseConfig();

        cancelAllTasks();
        queueInitialScheduler();
    }

    @Override
    public void onDisable() {
        cancelAllTasks();
        getLogger().info(getName() + " has been disabled!");
    }

    private void queueInitialScheduler() {
        if (configuration.getScheduledMessageMap().isEmpty()) {
            getLogger().info("Skipping adding the initial scheduler, no scheduled messages given");
            return;
        }

        var subtaskScheduler = new ScheduledMessageTaskScheduler(this, configuration.getScheduledMessageMap(), configuration.getQueueAheadDuration());

        getLogger().info("Queueing initial scheduler");

        long pollingTicks = tickConverter.durationToTicks(configuration.getPollingInterval());

        subtaskSchedulerTask = subtaskScheduler.runTaskTimer(this, 0L, pollingTicks);
    }

    private void cancelAllTasks() {
        getServer().getScheduler().cancelTasks(this);
    }
}
