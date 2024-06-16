package uk.firedev.skylight.modules.alias;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.message.string.StringReplacer;
import uk.firedev.skylight.Skylight;

import java.util.List;

public class CommandBuilder {

    private final String commandName;
    private final List<String> aliases;
    private final String permission;
    private final List<String> commands;
    private final List<String> messages;

    public CommandBuilder(String commandName, List<String> aliases, String permission, List<String> commands, List<String> messages) {
        this.commandName = commandName;
        this.aliases = aliases;
        this.permission = permission;
        this.commands = commands;
        this.messages = messages;
    }

    public void registerCommand() {
        if (commandName == null) {
            return;
        }
        CommandAPICommand command = new CommandAPICommand(commandName);
        command.withAliases(aliases.toArray(String[]::new));
        if (permission != null) {
            command.withPermission(CommandPermission.fromString(permission));
        }
        command.executes((sender, arguments) -> {
            messages.forEach(message -> new ComponentMessage(message).sendMessage(sender));
            commands.forEach(executeCommand -> {
                CommandSender thisSender;
                StringReplacer replacer = new StringReplacer();
                if (sender instanceof Player player) {
                    replacer.addReplacement("player", player.getName());
                }
                if (executeCommand.startsWith("console:")) {
                    executeCommand = executeCommand.replace("console:", "");
                    thisSender = Bukkit.getConsoleSender();
                } else {
                    thisSender = sender;
                }
                Bukkit.dispatchCommand(thisSender, replacer.replace(executeCommand));
            });
        });
        command.register();
    }

    public String getCommandName() {
        return commandName;
    }

}
