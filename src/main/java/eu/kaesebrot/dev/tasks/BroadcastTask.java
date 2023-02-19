package eu.kaesebrot.dev.tasks;

import org.bukkit.plugin.java.JavaPlugin;

public class BroadcastTask extends CronAnnouncerTask {

    public BroadcastTask(JavaPlugin plugin, String message) {
        super(plugin, message);
    }

    public BroadcastTask(JavaPlugin plugin, String message, int runs) {
        super(plugin, message, runs);
    }

    @Override
    public void run() {
        plugin.getServer().broadcastMessage(message);

        decrementCounterAndCancelIfDone();
    }
}
