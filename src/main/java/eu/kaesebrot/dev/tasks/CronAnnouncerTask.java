package eu.kaesebrot.dev.tasks;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class CronAnnouncerTask extends BukkitRunnable {
    protected final JavaPlugin plugin;
    protected final String message;
    protected int counter;
    private final boolean useCounter;

    public CronAnnouncerTask(JavaPlugin plugin, String message) {
        this.plugin = plugin;
        this.message = message;
        this.useCounter = false;
    }

    public CronAnnouncerTask(JavaPlugin plugin, String message, int runs) {
        this.plugin = plugin;
        this.message = message;
        this.useCounter = true;
        this.counter = runs;
    }

    protected void decrementCounterAndCancelIfDone() {
        if (useCounter) {
            counter--;
            if (counter <= 0) this.cancel();
        }
    }
}
