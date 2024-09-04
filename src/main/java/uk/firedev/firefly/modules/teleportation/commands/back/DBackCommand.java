package uk.firedev.firefly.modules.teleportation.commands.back;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.PlayerArgument;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;

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
                TeleportConfig.getInstance().getLocationInvalidMessage().sendMessage(player);
                return;
            }
            targetPlayer.teleportAsync(lastDeath).thenRun(() -> {
                TeleportConfig.getInstance().getDBackTeleportedMessage().sendMessage(targetPlayer);
                if (targetPlayer.getUniqueId() != player.getUniqueId()) {
                    ComponentReplacer replacer = new ComponentReplacer().addReplacement("target", targetPlayer.getName());
                    TeleportConfig.getInstance().getDBackTeleportedSenderMessage().applyReplacer(replacer).sendMessage(player);
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
                TeleportConfig.getInstance().getLocationInvalidMessage().sendMessage(console);
                return;
            }
            targetPlayer.teleportAsync(lastDeath).thenRun(() -> {
                TeleportConfig.getInstance().getDBackTeleportedMessage().sendMessage(targetPlayer);

                ComponentReplacer replacer = new ComponentReplacer().addReplacement("target", targetPlayer.getName());
                TeleportConfig.getInstance().getDBackTeleportedSenderMessage().applyReplacer(replacer).sendMessage(console);
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
                .withPermission(CommandPermission.fromString("firefly.command.dback.others"))
                .setOptional(true);
    }
    
}
