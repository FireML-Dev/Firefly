package uk.firedev.firefly.modules.teleportation.commands.spawn;

import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.PlayerArgument;
import uk.firedev.firefly.config.MessageConfig;
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
            // If target is the sender of the command, just teleport them
            if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
                TeleportManager.getInstance().sendToSpawn(false, targetPlayer, true);
                return;
            }
            // If not, teleport the target and tell the sender
            TeleportManager.getInstance().sendToSpawn(false, targetPlayer, player, true);
        });
        executesConsole((console, arguments) -> {
            Object playerArg = arguments.get("player");
            if (playerArg == null) {
                MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(console);
                return;
            }
            Player targetPlayer = (Player) playerArg;
            TeleportManager.getInstance().sendToSpawn(false, targetPlayer, console, true);
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
