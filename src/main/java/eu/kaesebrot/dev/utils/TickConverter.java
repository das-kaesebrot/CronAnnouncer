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

        long expectedFloor = expectedDifferenceTicks - ticksPerSecond;
        long expectedCeiling = expectedDifferenceTicks + ticksPerSecond;

        // check if actual difference ticks are in range of the expected ticks +/- one second to account for inaccuracies
        return (actualDifferenceTicks >= expectedFloor && actualDifferenceTicks <= expectedCeiling);
    }

    public List<Long> getNextRunTicksUntil(Cron cronInterval, ZonedDateTime searchStartDate, ZonedDateTime searchEndDate)
    {
        ZonedDateTime now = ZonedDateTime.now();
        List<Long> nextRunTicks = new ArrayList<>();

        ExecutionTime executionTime = ExecutionTime.forCron(cronInterval);
        var nextExecutionDates = executionTime.getExecutionDates(searchStartDate, searchEndDate);

        if (nextExecutionDates.isEmpty()) return nextRunTicks;

        for (var nextExecutionDate: nextExecutionDates)
        {
            nextRunTicks.add(durationToTicks(Duration.between(now, nextExecutionDate)));
        }

        return nextRunTicks;
    }

    public long durationToTicks(Duration duration) {
        return (duration.toMillis() * ticksPerSecond) / 1000;
    }
}
