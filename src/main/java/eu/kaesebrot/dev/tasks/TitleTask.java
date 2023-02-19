package eu.kaesebrot.dev.tasks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TitleTask extends CronAnnouncerTask {

    public TitleTask(JavaPlugin plugin, String message) {
        super(plugin, message);
    }

    public TitleTask(JavaPlugin plugin, String message, int runs) {
        super(plugin, message, runs);
    }


    @Override
    public void run() {
        for (Player player: plugin.getServer().getOnlinePlayers()) {
            player.sendTitle(message, null, 10, 70, 20);
        }

        decrementCounterAndCancelIfDone();
    }
}
