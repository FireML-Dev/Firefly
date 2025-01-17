package uk.firedev.firefly.modules.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.modules.command.CommandConfig;

import java.util.Objects;

public class GodmodeCommand extends Command {

    private final CommandTree command = new CommandTree(getName())
            .withPermission(getPermission())
            .executesPlayer(info -> {
                toggleGodmode(info.sender(), info.sender());
            })
            .then(
                    new EntitySelectorArgument.OnePlayer("target")
                            .executes((sender, arguments) -> {
                                Player player = (Player) Objects.requireNonNull(arguments.get("target"));
                                toggleGodmode(sender, player);
                            })
            );

    @Override
    public String getName() {
        return "godmode";
    }

    @Override
    public CommandTree getCommand() {
        return command;
    }

    private void toggleGodmode(@NotNull CommandSender sender, @NotNull Player target) {
        if (target.isInvulnerable()) {
            target.setInvulnerable(false);
            sendDisabledMessage(sender, target);
        } else {
            target.setInvulnerable(true);
            sendEnabledMessage(sender, target);
        }
    }

    private void sendEnabledMessage(@NotNull CommandSender sender, @NotNull Player target) {
        CommandConfig.getInstance().getGodmodeEnabledMessage().sendMessage(target);
        if (!target.equals(sender)) {
            CommandConfig.getInstance().getGodmodeEnabledSenderMessage()
                    .replace("target", target.name())
                    .sendMessage(sender);
        }
    }

    private void sendDisabledMessage(@NotNull CommandSender sender, @NotNull Player target) {
        CommandConfig.getInstance().getGodmodeDisabledMessage().sendMessage(target);
        if (!target.equals(sender)) {
            CommandConfig.getInstance().getGodmodeDisabledSenderMessage()
                    .replace("target", target.name())
                    .sendMessage(sender);
        }
    }

}
