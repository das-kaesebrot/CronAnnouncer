package eu.kaesebrot.dev;

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
    private BukkitTask subtaskScheduler;

    Map<String, ScheduledMessage> scheduledMessages;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        scheduleConfigParser = new ScheduleConfigParser(this);
        scheduledMessages = scheduleConfigParser.parseConfig();

        cancelAllTasks();
        queueInitialScheduler();
    }

    @Override
    public void onDisable() {
        cancelAllTasks();
        getLogger().info(getName() + " has been disabled!");
    }

    private void queueInitialScheduler() {
        if (scheduledMessages.isEmpty()) {
            getLogger().info("Skipping adding the initial scheduler, no scheduled messages given");
            return;
        }

        var queueAheadDuration = Duration.of(1, ChronoUnit.HOURS);
        var queuer = new ScheduledMessageTaskScheduler(this, scheduledMessages, queueAheadDuration);

        getLogger().info("Queueing initial scheduler");

        subtaskScheduler = queuer.runTaskTimer(this, 0L, tickConverter.durationToTicks(queueAheadDuration));
    }

    private void cancelAllTasks() {
        getServer().getScheduler().cancelTasks(this);
    }
}
