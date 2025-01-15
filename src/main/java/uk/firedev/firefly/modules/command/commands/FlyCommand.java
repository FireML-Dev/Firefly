package uk.firedev.firefly.modules.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.command.CommandConfig;
import uk.firedev.firefly.utils.CommandUtils;

public class FlyCommand implements SubModule {

    private boolean loaded;
    private final CommandTree command;

    public FlyCommand() {
        command = new CommandTree("fly")
                .withPermission("firefly.command.fly")
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
                    .replace("target", target.getName())
                    .sendMessage(sender);
        }
    }

    private void sendDisabledMessage(@NotNull CommandSender sender, @NotNull Player target) {
        CommandConfig.getInstance().getFlightDisabledMessage().sendMessage(target);
        if (!target.equals(sender)) {
            CommandConfig.getInstance().getFlightDisabledSenderMessage()
                    .replace("target", target.getName())
                    .sendMessage(sender);
        }
    }

    @Override
    public boolean isConfigEnabled() {
        return CommandConfig.getInstance().getConfig().getBoolean("fly.enabled", true);
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        command.register(Firefly.getInstance());
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        // There is nothing to reload here :)
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        CommandUtils.unregisterCommand("fly");
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

}
