package eu.kaesebrot.dev.tasks;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BroadcastTask extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final String message;

    public BroadcastTask(JavaPlugin plugin, String message) {
        this.plugin = plugin;
        this.message = message;
    }

    @Override
    public void run() {
        plugin.getServer().broadcastMessage(message);
    }
}
