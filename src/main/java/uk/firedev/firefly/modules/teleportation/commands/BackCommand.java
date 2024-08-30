package uk.firedev.firefly.modules.teleportation.commands;

import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.PlayerArgument;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.teleportation.TeleportManager;

public class BackCommand extends CommandAPICommand {

    private static BackCommand instance;

    private BackCommand() {
        super("back");
        withArguments(getPlayerArgument());
        setPermission(CommandPermission.fromString("firefly.command.back"));
        withShortDescription("Teleport to your last location");
        withFullDescription("Teleport to your last location");
        executesPlayer((player, arguments) -> {
            Player targetPlayer;
            Object playerArg = arguments.get("player");
            if (playerArg == null) {
                targetPlayer = player;
            } else {
                targetPlayer = (Player) playerArg;
            }
            targetPlayer.teleportAsync(TeleportManager.getInstance().getLastLocation(targetPlayer)).thenRun(() -> {
                // TODO proper message
                targetPlayer.sendMessage("Teleported to last location.");
                if (targetPlayer.getUniqueId() != player.getUniqueId()) {
                    // TODO proper message
                    player.sendMessage("Sent player to their last location.");
                }
            });
        });
        executesConsole((console, arguments) -> {
            Object playerArg = arguments.get("player");
            if (playerArg == null) {
                MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(console);
                return;
            }
            Player targetPlayer = (Player) playerArg;
            targetPlayer.teleportAsync(TeleportManager.getInstance().getLastLocation(targetPlayer)).thenRun(() -> {
                // TODO proper messages
                targetPlayer.sendMessage("Teleported to last location.");
                console.sendMessage("Sent player to their last location.");
            });
        });
    }

    public static BackCommand getInstance() {
        if (instance == null) {
            instance = new BackCommand();
        }
        return instance;
    }

    private Argument<Player> getPlayerArgument() {
        return new PlayerArgument("player")
                .withPermission(CommandPermission.fromString("firefly.command.back.others"))
                .setOptional(true);
    }

}
