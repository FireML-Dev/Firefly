package uk.firedev.firefly.modules.teleportation.commands.back;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;
import uk.firedev.firefly.utils.TeleportWarmup;

import java.util.Objects;

public class DBackCommand {

    public static CommandTree getCommand() {
        return new CommandTree("dback")
            .withPermission("firefly.command.dback")
            .withHelp("Teleport to your last death location", "Teleport to your last death location")
            .executesPlayer(info -> {
                teleportPlayer(info.sender(), info.sender());
            })
            .then(
                uk.firedev.daisylib.command.arguments.PlayerArgument.create("target")
                    .executes(info -> {
                        Player target = Objects.requireNonNull(info.args().getUnchecked("target"));
                        teleportPlayer(info.sender(), target);
                    })
            );
    }

    private static void teleportPlayer(@NotNull CommandSender sender, @NotNull Player target) {
        Location lastDeath = target.getLastDeathLocation();
        if (lastDeath == null) {
            TeleportConfig.getInstance().getLocationInvalidMessage().sendMessage(sender);
            return;
        }
        TeleportWarmup.teleportPlayer(
            target,
            lastDeath,
            TeleportConfig.getInstance().getBackWarmupSeconds()
        );
        // TODO this needs to be part of TeleportWarmup when ready
        TeleportConfig.getInstance().getDBackTeleportedSenderMessage()
            .replace("target", target.getName())
            .sendMessage(sender);
    }
    
}
