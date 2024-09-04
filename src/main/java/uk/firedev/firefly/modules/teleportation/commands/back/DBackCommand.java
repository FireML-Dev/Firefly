package uk.firedev.firefly.modules.teleportation.commands.back;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.PlayerArgument;
import uk.firedev.firefly.config.MessageConfig;

public class DBackCommand extends CommandAPICommand {

    private static DBackCommand instance;

    private DBackCommand() {
        super("dback");
        withArguments(getPlayerArgument());
        setPermission(CommandPermission.fromString("firefly.command.dback"));
        withShortDescription("Teleport to your last death location");
        withFullDescription("Teleport to your last death location");
        executesPlayer((player, arguments) -> {
            Player targetPlayer;
            Object playerArg = arguments.get("player");
            if (playerArg == null) {
                targetPlayer = player;
            } else {
                targetPlayer = (Player) playerArg;
            }
            Location lastDeath = targetPlayer.getLastDeathLocation();
            if (lastDeath == null) {
                // TODO last death doesn't exist message
                return;
            }
            targetPlayer.teleportAsync(lastDeath).thenRun(() -> {
                // TODO proper message
                targetPlayer.sendMessage("Teleported to last death location.");
                if (targetPlayer.getUniqueId() != player.getUniqueId()) {
                    // TODO proper message
                    player.sendMessage("Sent player to their last death location.");
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
            Location lastDeath = targetPlayer.getLastDeathLocation();
            if (lastDeath == null) {
                // TODO last death doesn't exist message
                return;
            }
            targetPlayer.teleportAsync(lastDeath).thenRun(() -> {
                // TODO proper messages
                targetPlayer.sendMessage("Teleported to last death location.");
                console.sendMessage("Sent player to their last death location.");
            });
        });
    }

    public static DBackCommand getInstance() {
        if (instance == null) {
            instance = new DBackCommand();
        }
        return instance;
    }

    private Argument<Player> getPlayerArgument() {
        return new PlayerArgument("player")
                .withPermission(CommandPermission.fromString("firefly.command.back.others"))
                .setOptional(true);
    }
    
}
