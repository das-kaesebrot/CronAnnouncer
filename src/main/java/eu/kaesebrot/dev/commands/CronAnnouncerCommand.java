package eu.kaesebrot.dev.commands;

import eu.kaesebrot.dev.CronAnnouncerPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CronAnnouncerCommand implements CommandExecutor {
    private final String KEY_ROOT = "schedules"; // keep this key in sync with ScheduleConfigParser!
    private final String SUBCOMMAND_LIST = "list";
    private final String SUBCOMMAND_ADD = "add";
    private final String SUBCOMMAND_REMOVE = "rm";
    private final String SUBCOMMAND_RELOAD = "reload";

    private final String PERMISSION_SEPARATOR = ".";
    private final String PERMISSION_ROOT = "eu.kaesebrot.dev.cronannouncer";
    private final String PERMISSION_LIST = PERMISSION_ROOT + PERMISSION_SEPARATOR + "list";
    private final String PERMISSION_ADD = PERMISSION_ROOT + PERMISSION_SEPARATOR + "add";
    private final String PERMISSION_REMOVE = PERMISSION_ROOT + PERMISSION_SEPARATOR + "remove";
    private final String PERMISSION_RELOAD = PERMISSION_ROOT + PERMISSION_SEPARATOR + "reload";
    private final CronAnnouncerPlugin plugin;

    public CronAnnouncerCommand(CronAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // don't handle if the args are empty or if the command doesn't come from a player
        if (!(sender instanceof Player player) || args.length == 0)
            return false;

        List<String> argsList = Arrays.asList(args);

        switch (argsList.get(0)) {
            case SUBCOMMAND_LIST -> {
                if (args.length == 1 && player.hasPermission(PERMISSION_LIST)) {
                    player.sendMessage(listRegisteredMessages());
                    return true;
                }
            }
            case SUBCOMMAND_ADD -> {
                // TODO
                if (args.length >= 1 && player.hasPermission(PERMISSION_ADD)) {
                    player.sendMessage("Not supported yet");
                    return true;
                }
            }
            case SUBCOMMAND_REMOVE -> {
                if (args.length == 2 && player.hasPermission(PERMISSION_REMOVE)) {
                    var messageKey = argsList.get(1).toLowerCase();
                    if (removeScheduledMessage(messageKey)) {
                        player.sendMessage(String.format("Successfully removed scheduled message '%s'", messageKey));
                        return true;
                    } else {
                        player.sendMessage(String.format("Failed removing scheduled message - no message found by id '%s'", messageKey));
                        return false;
                    }
                }
            }
            case SUBCOMMAND_RELOAD -> {
                if (args.length == 1 && player.hasPermission(PERMISSION_RELOAD)) {
                    plugin.init();
                    return true;
                }
            }
        }

        return false;
    }

    private String listRegisteredMessages()
    {
        var messages = plugin.getCronAnnouncerConfig().getScheduledMessageMap();

        if (messages.isEmpty())
            return "No scheduled messages registered (yet!)";

        StringBuilder returnText = new StringBuilder();
        returnText.append("Registered messages:\n");

        for (var message : messages.entrySet()) {
            returnText.append(String.format("[%s]\n%s\n", message.getKey(), message.getValue().toStringInCommand()));
        }

        return returnText.toString();
    }

    private boolean removeScheduledMessage(String messageKey)
    {
        var messages = plugin.getCronAnnouncerConfig().getScheduledMessageMap();

        if (messages.containsKey(messageKey)) {
            plugin.getConfig().set(String.format("%s.%s", KEY_ROOT, messageKey), null);
            plugin.saveConfig();
            plugin.init();
            return true;
        }

        return false;
    }
}
