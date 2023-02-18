package eu.kaesebrot.dev;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CronAnnouncerPlugin extends JavaPlugin {
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        getLogger().info(getName() + " has been enabled!");
    }
    @Override
    public void onDisable() {
        getLogger().info(getName() + " has been disabled!");
    }
}
