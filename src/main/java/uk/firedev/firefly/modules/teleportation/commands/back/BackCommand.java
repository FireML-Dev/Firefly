package uk.firedev.firefly.modules.teleportation.commands.back;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;
import uk.firedev.firefly.modules.teleportation.TeleportModule;

import java.util.Objects;

public class BackCommand {

    public static CommandTree getCommand() {
        return new CommandTree("back")
            .withPermission("firefly.command.back")
            .withHelp("Teleport to your last location", "Teleport to your last location")
            .executesPlayer(info -> {
                teleportPlayer(info.sender(), info.sender());
            })
            .then(
                PlayerArgument.create("target")
                    .executes(info -> {
                        Player target = Objects.requireNonNull(info.args().getUnchecked("target"));
                        teleportPlayer(info.sender(), target);
                    })
            );
    }

    private static void teleportPlayer(@NotNull CommandSender sender, @NotNull Player target) {
        Location location = TeleportModule.getInstance().getLastLocation(target);
        if (location == null) {
            TeleportConfig.getInstance().getLocationInvalidMessage().sendMessage(sender);
            return;
        }
        target.teleportAsync(location).thenAccept(success -> {
            if (success) {
                TeleportConfig.getInstance().getBackTeleportedMessage().sendMessage(target);
                if (target != sender) {
                    ComponentReplacer replacer = ComponentReplacer.create("target", target.getName());
                    TeleportConfig.getInstance().getBackTeleportedSenderMessage().applyReplacer(replacer).sendMessage(sender);
                }
            } else {
                TeleportConfig.getInstance().getLocationInvalidMessage().sendMessage(sender);
            }
        });
    }

}
