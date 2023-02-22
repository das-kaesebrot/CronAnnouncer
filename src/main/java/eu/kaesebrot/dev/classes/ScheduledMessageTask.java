package eu.kaesebrot.dev.classes;

import org.bukkit.scheduler.BukkitTask;

public record ScheduledMessageTask(BukkitTask task, long absoluteEndTicks) {

    public void cancelIfEndTicksHavePassed(long absoluteTicksNow) {
        if (absoluteTicksNow > absoluteEndTicks && !task.isCancelled()) {
            task.cancel();
        }
    }
}
