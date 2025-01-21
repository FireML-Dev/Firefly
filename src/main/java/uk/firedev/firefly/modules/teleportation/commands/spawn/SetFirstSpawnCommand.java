package uk.firedev.firefly.modules.teleportation.commands.spawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.LocationArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.LocationType;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;

public class SetFirstSpawnCommand extends CommandAPICommand {

    private static SetFirstSpawnCommand instance;

    private SetFirstSpawnCommand() {
        super("setfirstspawn");
        withArguments(
                new LocationArgument("location", LocationType.PRECISE_POSITION, false).setOptional(true)
        );
        setPermission(CommandPermission.fromString("firefly.command.setfirstspawn"));
        withShortDescription("Set the server first spawn location");
        withFullDescription("Set the server first spawn location");
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
            TeleportConfig.getInstance().setSpawnLocation(true, location);
            TeleportConfig.getInstance().getSpawnSetFirstSpawnMessage().sendMessage(sender);
        });
    }

    public static SetFirstSpawnCommand getInstance() {
        if (instance == null) {
            instance = new SetFirstSpawnCommand();
        }
        return instance;
    }

}
