package eu.kaesebrot.dev.tasks;

import eu.kaesebrot.dev.classes.ScheduledMessage;
import eu.kaesebrot.dev.utils.TickConverter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduledMessageTaskQueuer extends BukkitRunnable
{
    private final JavaPlugin plugin;
    private Map<String, ScheduledMessage> scheduledMessages;
    private Duration durationAhead;
    private List<Integer> activeSubTasks = new ArrayList<>();
    private TickConverter tickConverter = new TickConverter();

    public ScheduledMessageTaskQueuer(JavaPlugin plugin, Map<String, ScheduledMessage> scheduledMessages, Duration durationAhead) {
        this.plugin = plugin;
        this.scheduledMessages = scheduledMessages;
        this.durationAhead = durationAhead;
    }

    @Override
    public void run()
    {
        cleanUpRunningSubTasks();

        for (var scheduledMessage: scheduledMessages.entrySet()) {
            var message = scheduledMessage.getValue();
            var messageText = message.getText();

            plugin.getLogger().info(String.format("Scheduling messages for '%s' for duration %s", scheduledMessage.getKey(), durationAhead.toString()));
            var nextRunTicksForMessage = tickConverter.getNextRunTicksForNextDurationFromNow(scheduledMessage.getValue().getSchedule(), durationAhead);

            for (long nextRunTick: nextRunTicksForMessage)
            {
                var subTaskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new BroadcastTask(plugin, messageText), nextRunTick);
                var nextRunDateTime = tickConverter.ticksToDateTimeFromNow(nextRunTick);
                activeSubTasks.add(subTaskId);
                plugin.getLogger().info(String.format("Scheduled message '%s' at %s (%s ticks from now)", scheduledMessage.getKey(), nextRunDateTime.toString(), nextRunTick));
            }
        }
    }

    private void cleanUpRunningSubTasks() {
        for (int taskId: activeSubTasks)
        {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
    }
}
