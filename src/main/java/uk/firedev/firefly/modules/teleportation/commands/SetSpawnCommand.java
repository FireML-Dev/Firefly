package uk.firedev.firefly.modules.teleportation.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.LocationArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.LocationType;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;

public class SetSpawnCommand extends CommandAPICommand {

    private static SetSpawnCommand instance;

    private SetSpawnCommand() {
        super("setspawn");
        withArguments(
                new LocationArgument("location", LocationType.PRECISE_POSITION, false).setOptional(true)
        );
        setPermission(CommandPermission.fromString("firefly.command.setspawn"));
        withShortDescription("Set the server spawn location");
        withFullDescription("Set the server spawn location");
        executes((sender, arguments) -> {
            Location location;
            Object locationArg = arguments.get("location");
            if (locationArg == null) {
                if (!(sender instanceof Player player)) {
                    ComponentMessage.fromString("Invalid location specified").sendMessage(sender);
                    return;
                }
                location = player.getLocation();
            } else {
                location = (Location) locationArg;
            }
            TeleportConfig.getInstance().setSpawnLocation(false, location);
            // TODO send set spawn message
        });
    }

    public static SetSpawnCommand getInstance() {
        if (instance == null) {
            instance = new SetSpawnCommand();
        }
        return instance;
    }
    
}
