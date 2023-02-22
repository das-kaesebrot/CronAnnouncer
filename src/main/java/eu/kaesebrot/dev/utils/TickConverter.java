package eu.kaesebrot.dev.utils;

import com.cronutils.model.Cron;
import com.cronutils.model.time.ExecutionTime;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TickConverter
{
    private static final int ticksPerSecond = 20;

    public static int getTicksPerSecond() {
        return ticksPerSecond;
    }

    public Optional<Long> ticksRepeatableInterval(List<Long> tickList) {
        long ticklistSize = tickList.size();

        if (ticklistSize <= 1) {
            return Optional.empty();
        }

        long offsetStart = tickList.get(0);
        long ticksBetween = tickList.get(1) - tickList.get(0);

        for (int index = 0; index < ticklistSize; index++) {
            var estimatedTicks = offsetStart + (ticksBetween * index);

            if (estimatedTicks != tickList.get(index)) {
                return Optional.empty();
            }
        }

        return Optional.of(ticksBetween);
    }

    public ZonedDateTime ticksToDateTimeFromNow(long ticks)
    {
        return ZonedDateTime.now().plus(Duration.of(ticks / ticksPerSecond, ChronoUnit.SECONDS));
    }

    public boolean ticksAreSync(long firstAbsoluteTicks, ZonedDateTime firstDateTime, long secondAbsoluteTicks, ZonedDateTime secondDateTime) {
        long expectedDifferenceTicks = durationToTicks(Duration.between(firstDateTime, secondDateTime));
        long actualDifferenceTicks = secondAbsoluteTicks - firstAbsoluteTicks;

        // check if actual difference ticks are in range of the expected ticks +/- one second to account for inaccuracies
        return !((actualDifferenceTicks >= expectedDifferenceTicks - ticksPerSecond) && (actualDifferenceTicks <= expectedDifferenceTicks + ticksPerSecond));
    }

    public List<Long> getNextRunTicksForNextDurationFromNow(Cron cronInterval, Duration duration)
    {
        var now = ZonedDateTime.now();
        return getNextRunTicksUntil(cronInterval, now, now.plus(duration));
    }

    public List<Long> getNextRunTicksUntil(Cron cronInterval, ZonedDateTime searchStartDate, ZonedDateTime searchEndDate)
    {
        List<Long> nextRunTicks = new ArrayList<>();

        ExecutionTime executionTime = ExecutionTime.forCron(cronInterval);
        var nextExecutionDates = executionTime.getExecutionDates(searchStartDate, searchEndDate);

        if (nextExecutionDates.isEmpty()) {
            throw new IllegalArgumentException("Time to next execution can't be in the past!");
        }

        for (var nextExecutionDate: nextExecutionDates)
        {
            nextRunTicks.add(durationToTicks(Duration.between(searchStartDate, nextExecutionDate)));
        }

        return nextRunTicks;
    }

    public long getNextRunTimeTicks(Cron cronInterval)
    {
        ZonedDateTime now = ZonedDateTime.now();
        ExecutionTime executionTime = ExecutionTime.forCron(cronInterval);
        var timeToNextExecution = executionTime.timeToNextExecution(now);

        if (timeToNextExecution.isEmpty()) {
            throw new IllegalArgumentException("Time to next execution can't be in the past!");
        }

        return durationToTicks(timeToNextExecution.get());
    }

    public long durationToTicks(Duration duration) {
        return (duration.toMillis() * ticksPerSecond) / 1000;
    }
}
