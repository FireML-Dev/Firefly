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

public class SetFirstSpawnCommand {

    public static CommandTree getCommand() {
        return new CommandTree("setfirstspawn")
            .withPermission("firefly.command.setfirstspawn")
            .withHelp("Set the server first spawn location", "Set the server first spawn location")
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
        TeleportConfig.getInstance().setSpawnLocation(true, location);
        TeleportConfig.getInstance().getSpawnSetFirstSpawnMessage().sendMessage(sender);
    }

}
