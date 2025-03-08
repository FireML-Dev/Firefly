package uk.firedev.firefly.modules.teleportation.commands.spawn;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.LocationArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.LocationType;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;

import java.util.Objects;

public class SetSpawnCommand {

    public static CommandTree getCommand() {
        return new CommandTree("setspawn")
            .withPermission("firefly.command.setspawn")
            .withHelp("Set the server spawn location", "Set the server spawn location")
            .executesPlayer(info -> {
                setLocation(info.sender(), info.sender().getLocation());
            })
            .then(
                new LocationArgument("location")
                    .executes(info -> {
                        Location location = Objects.requireNonNull(info.args().getUnchecked("location"));
                        setLocation(info.sender(), location);
                    })
            );
    }

    private static void setLocation(@NotNull CommandSender sender, @NotNull Location location) {
        TeleportConfig.getInstance().setSpawnLocation(false, location);
        TeleportConfig.getInstance().getSpawnSetSpawnMessage().sendMessage(sender);
    }
    
}
