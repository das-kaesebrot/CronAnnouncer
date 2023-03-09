package eu.kaesebrot.dev;

import eu.kaesebrot.dev.classes.CronAnnouncerConfiguration;
import eu.kaesebrot.dev.commands.CronAnnouncerCommand;
import eu.kaesebrot.dev.tasks.ScheduledMessageTaskScheduler;
import eu.kaesebrot.dev.utils.ScheduleConfigParser;
import eu.kaesebrot.dev.utils.TickConverter;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class CronAnnouncerPlugin extends JavaPlugin {
    private ScheduleConfigParser scheduleConfigParser;
    private final TickConverter tickConverter = new TickConverter();
    private BukkitTask subtaskSchedulerTask;
    private CronAnnouncerConfiguration configuration;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        scheduleConfigParser = new ScheduleConfigParser(this);
        TabExecutor command = new CronAnnouncerCommand(this);

        this.getCommand("cronannouncer").setExecutor(command);
        this.getCommand("cronannouncer").setTabCompleter(command);

        init(); // innit mate
    }

    @Override
    public void onDisable() {
        cancelAllTasks();
        getLogger().info(getName() + " has been disabled!");
    }

    public void init() {
        reloadCronAnnouncerConfig();
        cancelAllTasks();
        queueInitialScheduler();
    }

    public CronAnnouncerConfiguration getCronAnnouncerConfig() {
        return this.configuration;
    }

    public void reloadCronAnnouncerConfig() {
        this.reloadConfig();
        configuration = scheduleConfigParser.parseConfig();
    }

    private void queueInitialScheduler() {
        if (configuration.getScheduledMessageMap().isEmpty()) {
            getLogger().info("Skipping adding the initial scheduler, no scheduled messages given");
            return;
        }

        var subtaskScheduler = new ScheduledMessageTaskScheduler(this, configuration.getScheduledMessageMap(), configuration.getQueueAheadDuration());

        getLogger().info(String.format("Queueing initial scheduler with polling interval %s and queue duration %s",
                configuration.getPollingInterval(), configuration.getQueueAheadDuration()));

        long pollingTicks = tickConverter.durationToTicks(configuration.getPollingInterval());

        subtaskSchedulerTask = subtaskScheduler.runTaskTimer(this, 0L, pollingTicks);
    }

    private void cancelAllTasks() {
        getServer().getScheduler().cancelTasks(this);
    }
}
