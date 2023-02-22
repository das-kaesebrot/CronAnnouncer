package eu.kaesebrot.dev.tasks;

import eu.kaesebrot.dev.classes.ScheduledMessage;
import eu.kaesebrot.dev.classes.ScheduledMessageTask;
import eu.kaesebrot.dev.classes.TickReferencePoint;
import eu.kaesebrot.dev.utils.TickConverter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduledMessageTaskScheduler extends BukkitRunnable
{
    private final JavaPlugin plugin;
    private final Map<String, ScheduledMessage> scheduledMessages;
    private final Duration durationAhead;
    private final List<ScheduledMessageTask> activeSubTasks = new ArrayList<>();
    private final TickConverter tickConverter = new TickConverter();
    private TickReferencePoint referencePoint;
    private ZonedDateTime lastScheduleAheadUntil;

    public ScheduledMessageTaskScheduler(JavaPlugin plugin, Map<String, ScheduledMessage> scheduledMessages, Duration durationAhead) {
        this.plugin = plugin;
        this.scheduledMessages = scheduledMessages;
        this.durationAhead = durationAhead;
        lastScheduleAheadUntil = ZonedDateTime.now();

        updateReference();
    }

    @Override
    public void run()
    {
        cleanUpPastRunningSubtasks();

        var now = ZonedDateTime.now();

        if (!ticksAreSync()) {
            cleanUpAllRunningSubTasks();

            // reset last scheduling end timeframe to ensure we re-queue all tasks again with the correct timestamps
            lastScheduleAheadUntil = now;
        }

        // use lastScheduleAheadUntil if it's in the future, use now if last schedule was in the past
        // if it's in the past, we're probably in the first ever run
        var searchDateStart = (Duration.between(now, lastScheduleAheadUntil).isNegative() ? now : lastScheduleAheadUntil);
        var searchDateEnd = now.plus(durationAhead);

        for (var scheduledMessage: scheduledMessages.entrySet()) {
            var message = scheduledMessage.getValue();

            var nextRunTicksForMessage = tickConverter.getNextRunTicksUntil(scheduledMessage.getValue().getSchedule(),
                    searchDateStart, searchDateEnd);

            if (nextRunTicksForMessage.isEmpty())
                break;

            plugin.getLogger().info(String.format("Scheduling new messages for %s=%s in time slot %s to %s",
                    scheduledMessage.getKey(), scheduledMessage.getValue().toString(), searchDateStart, searchDateEnd));

            // guess if we can use a simple repeatable timer
            // if present, we can, otherwise we have to queue single tasks for every iteration
            var ticksRepeatableInterval = tickConverter.ticksRepeatableInterval(nextRunTicksForMessage);

            if (ticksRepeatableInterval.isPresent()) {
                var subTask = getRunnableForMessage(message).runTaskTimer(plugin, nextRunTicksForMessage.get(0), ticksRepeatableInterval.get());

                activeSubTasks.add(new ScheduledMessageTask(subTask, getAbsoluteTicks() + nextRunTicksForMessage.get(nextRunTicksForMessage.size() - 1)));

                plugin.getLogger().info(String.format("Scheduled repeatable message %s=%s running %s times every %s ticks, first run %s ticks from now",
                        scheduledMessage.getKey(), message, nextRunTicksForMessage.size(), ticksRepeatableInterval.get(), nextRunTicksForMessage.get(0)));
            }
            else
            {
                for (long nextRunTick: nextRunTicksForMessage)
                {
                    var subTask = getRunnableForMessage(message).runTaskLater(plugin, nextRunTick);

                    var nextRunDateTime = tickConverter.ticksToDateTimeFromNow(nextRunTick);

                    activeSubTasks.add(new ScheduledMessageTask(subTask, getAbsoluteTicks() + nextRunTick));

                    plugin.getLogger().info(String.format("Scheduled single-run message %s=%s at %s (%s ticks from now)",
                            scheduledMessage.getKey(), message, nextRunDateTime.toString(), nextRunTick));
                }
            }
        }

        lastScheduleAheadUntil = searchDateEnd;
    }

    private void cleanUpAllRunningSubTasks() {
        for (ScheduledMessageTask task: activeSubTasks)
        {
            task.task().cancel();
        }

        activeSubTasks.clear();
    }

    private void cleanUpPastRunningSubtasks() {
        for (ScheduledMessageTask task: activeSubTasks)
        {
            task.cancelIfEndTicksHavePassed(getAbsoluteTicks() + TickConverter.getTicksPerSecond());
        }

        activeSubTasks.removeIf(s -> s.task().isCancelled());
    }

    private boolean ticksAreSync() {
        var areSync = tickConverter.ticksAreSync(
                referencePoint.getTicks(), referencePoint.getDateTime(),
                getAbsoluteTicks(), ZonedDateTime.now());

        if (!areSync) {
            plugin.getLogger().warning("Detected sync loss between ticks and real time!");
            updateReference();
        }

        return areSync;
    }

    private TickReferencePoint getReferencePointForNow() {
        return new TickReferencePoint(getAbsoluteTicks(), ZonedDateTime.now());
    }

    // Update our reference point for checking if we're still synced to real time
    private void updateReference() {
        if (referencePoint == null) {
            referencePoint = getReferencePointForNow();
            return;
        }

        referencePoint.setTicks(getAbsoluteTicks());
        referencePoint.setDateTime(ZonedDateTime.now());
    }

    private BukkitRunnable getRunnableForMessage(ScheduledMessage message) {
        return switch (message.getType()) {
            case TITLE -> new TitleTask(plugin, message.getText());
            case BROADCAST -> new BroadcastTask(plugin, message.getText());
            default -> throw new IllegalArgumentException("Illegal message type provided");
        };
    }

    private long getAbsoluteTicks() {
        return plugin.getServer().getWorlds().get(0).getFullTime();
    }
}
