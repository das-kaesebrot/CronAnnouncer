package eu.kaesebrot.dev.tasks;

import eu.kaesebrot.dev.classes.ScheduledMessage;
import eu.kaesebrot.dev.utils.TickConverter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduledMessageTaskQueuer extends BukkitRunnable
{
    private final JavaPlugin plugin;
    private Map<String, ScheduledMessage> scheduledMessages;
    private Duration durationAhead;
    private List<BukkitTask> activeSubTasks = new ArrayList<>();
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

            plugin.getLogger().info(String.format("Scheduling messages for %s=%s for duration %s", scheduledMessage.getKey(), scheduledMessage.getValue().toString(), durationAhead.toString()));
            var nextRunTicksForMessage = tickConverter.getNextRunTicksForNextDurationFromNow(scheduledMessage.getValue().getSchedule(), durationAhead);

            // guess if we can use a simple repeatable timer
            // if present, we can, otherwise we have to queue single tasks for every iteration
            var ticksRepeatableInterval = tickConverter.ticksRepeatableInterval(nextRunTicksForMessage);

            BukkitRunnable runnable;

            if (ticksRepeatableInterval.isPresent()) {
                switch (message.getType()) {
                    case TITLE:
                        runnable = new TitleTask(plugin, messageText, nextRunTicksForMessage.size());
                        break;
                    case BROADCAST:
                        runnable = new BroadcastTask(plugin, messageText, nextRunTicksForMessage.size());
                        break;
                    default:
                        throw new IllegalArgumentException("Illegal message type provided");
                }

                var subTask = runnable.runTaskTimer(plugin, nextRunTicksForMessage.get(0), ticksRepeatableInterval.get());

                activeSubTasks.add(subTask);

                plugin.getLogger().info(String.format("Scheduled repeatable message %s=%s running %s times every %s ticks, first run %s ticks from now",
                        scheduledMessage.getKey(), message, nextRunTicksForMessage.size(), ticksRepeatableInterval.get(), nextRunTicksForMessage.get(0)));
            }
            else
            {
                for (long nextRunTick: nextRunTicksForMessage)
                {
                    switch (message.getType()) {
                        case TITLE:
                            runnable = new TitleTask(plugin, messageText);
                            break;
                        case BROADCAST:
                            runnable = new BroadcastTask(plugin, messageText);
                            break;
                        default:
                            throw new IllegalArgumentException("Illegal message type provided");
                    }

                    var subTask = runnable.runTaskLater(plugin, nextRunTick);

                    var nextRunDateTime = tickConverter.ticksToDateTimeFromNow(nextRunTick);
                    activeSubTasks.add(subTask);

                    plugin.getLogger().info(String.format("Scheduled single-run message %s=%s at %s (%s ticks from now)",
                            scheduledMessage.getKey(), message.toString(), nextRunDateTime.toString(), nextRunTick));
                }
            }
        }
    }

    private void cleanUpRunningSubTasks() {
        for (BukkitTask task: activeSubTasks)
        {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
    }
}
