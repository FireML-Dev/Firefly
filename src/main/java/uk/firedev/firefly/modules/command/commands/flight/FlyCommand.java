package uk.firedev.firefly.modules.command.commands.flight;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.modules.command.CommandConfig;
import uk.firedev.firefly.placeholders.Placeholders;

import java.util.Objects;

public class FlyCommand extends Command {

    @NotNull
    @Override
    public String getConfigName() {
        return "fly";
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

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider -> {
            provider.addAudiencePlaceholder("can_fly", audience -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                return Component.text(player.getAllowFlight());
            });
        });
    }

    @NotNull
    @Override
    public CommandTree refreshCommand() {
        return new CommandTree(getName())
                .withPermission(getPermission())
                .withAliases(getAliases())
                .withShortDescription("Toggles flight")
                .executesPlayer(info -> {
                    toggleFlight(info.sender(), info.sender());
                })
                .then(
                        new EntitySelectorArgument.OnePlayer("target")
                                .withPermission(getTargetPermission())
                                .executes((sender, arguments) -> {
                                    // This should never be null.
                                    Player player = (Player) Objects.requireNonNull(arguments.get("target"));
                                    toggleFlight(sender, player);
                                })
                );
    }

}
