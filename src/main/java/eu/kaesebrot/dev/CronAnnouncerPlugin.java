package eu.kaesebrot.dev;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class CronAnnouncerPlugin extends JavaPlugin {
    FileConfiguration config;
    Logger logger;

    public CronAnnouncerPlugin() {
        config = getConfig();
        logger = getLogger();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
    }
    @Override
    public void onDisable() {
        logger.info(getName() + " has been disabled!");
    }
}
