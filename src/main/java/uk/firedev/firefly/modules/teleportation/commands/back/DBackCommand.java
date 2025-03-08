package uk.firedev.firefly.modules.teleportation.commands.back;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.PlayerArgument;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;
import uk.firedev.firefly.modules.teleportation.TeleportModule;

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
        target.teleportAsync(lastDeath).thenAccept(success -> {
            if (success) {
                TeleportConfig.getInstance().getDBackTeleportedMessage().sendMessage(target);
                if (target != sender) {
                    ComponentReplacer replacer = ComponentReplacer.create("target", target.getName());
                    TeleportConfig.getInstance().getDBackTeleportedSenderMessage().applyReplacer(replacer).sendMessage(sender);
                }
            } else {
                TeleportConfig.getInstance().getLocationInvalidMessage().sendMessage(sender);
            }
        });
    }
    
}
