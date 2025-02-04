package uk.firedev.firefly.modules.customalias;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.api.message.string.StringReplacer;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.firefly.Firefly;

import java.util.List;
import java.util.Objects;

public class CommandBuilder {

    private final String commandName;
    private final List<String> aliases;
    private final String permission;
    private final List<String> commands;
    private final List<String> messages;

    public CommandBuilder(@NotNull ConfigurationSection section) {
        this.commandName = Objects.requireNonNull(section.getName());
        this.aliases = section.getStringList("aliases");
        this.permission = section.getString("permission");
        this.commands = section.getStringList("commands");
        this.messages = section.getStringList("messages");
    }

    public void registerCommand() {
        if (commandName == null) {
            return;
        }
        new CommandAPICommand(commandName)
                .withAliases(aliases.toArray(String[]::new))
                .withPermission(permission == null ? CommandPermission.NONE : CommandPermission.fromString(permission))
                .executes((sender, arguments) -> {
                    messages.forEach(message -> ComponentMessage.fromString(message).sendMessage(sender));
                    commands.forEach(executeCommand -> {
                        CommandSender thisSender;
                        StringReplacer replacer = StringReplacer.create();
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
                })
                .register(Firefly.getInstance());
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getAliases() {
        return aliases;
    }

}
