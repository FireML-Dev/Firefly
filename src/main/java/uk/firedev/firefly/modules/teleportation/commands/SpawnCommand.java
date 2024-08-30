package uk.firedev.firefly.modules.teleportation.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.LocationArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.LocationType;
import uk.firedev.daisylib.libs.commandapi.arguments.PlayerArgument;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;
import uk.firedev.firefly.modules.teleportation.TeleportManager;

public class SpawnCommand extends CommandAPICommand {

    private static SpawnCommand instance;

    private SpawnCommand() {
        super("spawn");
        withArguments(getPlayerArgument());
        setPermission(CommandPermission.fromString("firefly.command.spawn"));
        withShortDescription("Teleport to spawn");
        withFullDescription("Teleport to spawn");
        executesPlayer((player, arguments) -> {
            Player targetPlayer;
            Object playerArg = arguments.get("player");
            if (playerArg == null) {
                targetPlayer = player;
            } else {
                targetPlayer = (Player) playerArg;
            }
            targetPlayer.teleportAsync(TeleportManager.getInstance().getSpawnLocation());
            // TODO send teleported to spawn message to target
            // TODO if target is not sender, tell the sender too
        });
    }

    public static SpawnCommand getInstance() {
        if (instance == null) {
            instance = new SpawnCommand();
        }
        return instance;
    }

    private Argument<Player> getPlayerArgument() {
        return new PlayerArgument("player")
                .withPermission(CommandPermission.fromString("firefly.command.spawn.others"))
                .setOptional(true);
    }

}
