package eu.kaesebrot.dev.commands;

import eu.kaesebrot.dev.CronAnnouncerPlugin;
import eu.kaesebrot.dev.classes.MessageType;
import eu.kaesebrot.dev.utils.ScheduleConfigParser;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CronAnnouncerCommand implements TabExecutor {
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
                // command: /cronannouncer add name "schedule" "message" type
                argsList = correctlyParseArgsWithQuotes(argsList);

                if (argsList.size() == 5 && player.hasPermission(PERMISSION_ADD)) {
                    String key = argsList.get(1).replaceAll("^\"|\"$", "");
                    String schedule = argsList.get(2);
                    String message = argsList.get(3);
                    String type = argsList.get(4);

                    if (plugin.getCronAnnouncerConfig().getScheduledMessageMap().containsKey(key)) {
                        player.sendMessage(String.format("Key '%s' already exists, please pick another name", key));
                        return false;
                    }

                    try {
                        var newMessage = new ScheduleConfigParser(plugin).parseFromStrings(schedule, message, type);

                        plugin.getLogger().info(String.format("Added new message via command: %s", newMessage.toString()));

                        plugin.getConfig().set(String.format("%s.%s", KEY_ROOT, key), newMessage.asStringMap());
                        plugin.saveConfig();
                        plugin.init();

                        player.sendMessage(String.format("Successfully added new message:\n[%s]\n%s\n", key, newMessage.toStringInCommand()));

                        return true;

                    } catch (Exception e) {
                        player.sendMessage("Error while parsing!\nSyntax: /cronannouncer add <unique-name> \"<cron-expression>\" \"<message-text>\" <type>");
                        return false;
                    }
                }
            }
            case SUBCOMMAND_REMOVE -> {
                if (args.length == 2 && player.hasPermission(PERMISSION_REMOVE)) {
                    var messageKey = argsList.get(1).toLowerCase();
                    if (removeScheduledMessage(messageKey)) {
                        player.sendMessage(String.format("Successfully removed scheduled message '%s'", messageKey));
                        return true;
                    }

                    player.sendMessage(String.format("Failed removing scheduled message - no message found by key '%s'", messageKey));
                    return false;
                }
            }
            case SUBCOMMAND_RELOAD -> {
                if (args.length == 1 && player.hasPermission(PERMISSION_RELOAD)) {
                    plugin.init();
                    player.sendMessage("Reload successful");
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestedArgs = new ArrayList<>();

        // don't handle if the args are empty or if the command doesn't come from a player
        if (!(sender instanceof Player player))
            return List.of();

        var parsedArgesWithQuotes = correctlyParseArgsWithQuotes(Arrays.asList(args));

        // /cron
        if (args.length == 1) {
            if (player.hasPermission(PERMISSION_LIST)) suggestedArgs.add(SUBCOMMAND_LIST);
            if (player.hasPermission(PERMISSION_ADD)) suggestedArgs.add(SUBCOMMAND_ADD);
            if (player.hasPermission(PERMISSION_REMOVE)) suggestedArgs.add(SUBCOMMAND_REMOVE);
            if (player.hasPermission(PERMISSION_RELOAD)) suggestedArgs.add(SUBCOMMAND_RELOAD);

            return suggestedArgs;

        }
        // /cron [cmd]
        else if (args.length == 2)
        {
            switch (args[0]) {
                case SUBCOMMAND_REMOVE:
                    if (player.hasPermission(PERMISSION_REMOVE)) {
                        suggestedArgs.addAll(plugin.getCronAnnouncerConfig().getScheduledMessageMap()
                                .keySet());
                        return suggestedArgs;
                    }

                    // no break intended

                case SUBCOMMAND_LIST:
                case SUBCOMMAND_RELOAD:
                case SUBCOMMAND_ADD:
                    break;
            }
        }
        // /cron [cmd] [arg] [arg] [arg]
        else if (parsedArgesWithQuotes.size() == 5) {
            // /cron add myname "<cron-expr>" "<message>" <type auto-completion here>
            if (args[0].equals(SUBCOMMAND_ADD)
                    && player.hasPermission(PERMISSION_ADD))
            {
                return MessageType.getValuesAsLowercase().toList();
            }
        }

        return List.of();
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

    private List<String> correctlyParseArgsWithQuotes(List<String> args) {

        List<String> parsedArgs = new ArrayList<>();
        String argsString = String.join(" ", args).trim() + " "; // add a space to the end to cover the last element

        if (argsString.length() <= 1) return parsedArgs;

        boolean quoteFound = false;
        int lastQuoteIndex = 0;
        int lastSpaceIndex = -1;

        for (int index = 0; index < argsString.length(); index++) {
            if (argsString.charAt(index) == '\"') {
                if (quoteFound) {
                    parsedArgs.add(argsString.substring(lastQuoteIndex + 1, index));
                } else {
                    lastQuoteIndex = index;
                }

                quoteFound = !quoteFound;
            }

            if (!quoteFound
                    && argsString.charAt(index) == ' '
                    && argsString.charAt(index - 1) != '\"') {
                parsedArgs.add(argsString.substring(lastSpaceIndex + 1, index));
            }

            if (argsString.charAt(index) == ' ') {
                lastSpaceIndex = index;
            }
        }

        // plugin.getLogger().info(parsedArgs.toString());

        return parsedArgs;
    }
}
