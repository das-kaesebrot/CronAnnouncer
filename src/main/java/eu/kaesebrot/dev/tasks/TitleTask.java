package eu.kaesebrot.dev.tasks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TitleTask extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final String message;

    public TitleTask(JavaPlugin plugin, String message) {
        this.plugin = plugin;
        this.message = message;
    }


    @Override
    public void run() {
        for (Player player: plugin.getServer().getOnlinePlayers()) {
            player.sendTitle(null, message, 10, 70, 20);
        }
    }
}
