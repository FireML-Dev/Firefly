package uk.firedev.firefly.modules.command.commands.flight;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.modules.command.CommandConfig;

public class FlyCommand extends Command {

    private final CommandTree command = new CommandTree(getName())
            .withPermission(getPermission())
            .withShortDescription("Toggles flight")
            .executesPlayer((player, arguments) -> {
                toggleFlight(player, player);
            })
            .then(
                    new EntitySelectorArgument.OnePlayer("target")
                            .executes((sender, arguments) -> {
                                Player player = (Player) arguments.get("target");
                                if (player == null) {
                                    MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(sender);
                                    return;
                                }
                                toggleFlight(sender, player);
                            })
            );

    @Override
    public String getName() {
        return "fly";
    }

    @Override
    public CommandTree getCommand() {
        return command;
    }

    private void toggleFlight(@NotNull CommandSender sender, @NotNull Player target) {
        if (target.getAllowFlight()) {
            target.setFlying(false);
            target.setAllowFlight(false);
            sendDisabledMessage(sender, target);
        } else {
            target.setAllowFlight(true);
            sendEnabledMessage(sender, target);
        }
    }

    private void sendEnabledMessage(@NotNull CommandSender sender, @NotNull Player target) {
        CommandConfig.getInstance().getFlightEnabledMessage().sendMessage(target);
        if (!target.equals(sender)) {
            CommandConfig.getInstance().getFlightEnabledSenderMessage()
                    .replace("target", target.name())
                    .sendMessage(sender);
        }
    }

    private void sendDisabledMessage(@NotNull CommandSender sender, @NotNull Player target) {
        CommandConfig.getInstance().getFlightDisabledMessage().sendMessage(target);
        if (!target.equals(sender)) {
            CommandConfig.getInstance().getFlightDisabledSenderMessage()
                    .replace("target", target.name())
                    .sendMessage(sender);
        }
    }

}
