package uk.firedev.firefly.modules.teleportation.commands.back;

import org.bukkit.entity.Player;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.PlayerArgument;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;
import uk.firedev.firefly.modules.teleportation.TeleportModule;

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
            targetPlayer.teleportAsync(TeleportModule.getInstance().getLastLocation(targetPlayer)).thenAccept(success -> {
                if (success) {
                    TeleportConfig.getInstance().getBackTeleportedMessage().sendMessage(targetPlayer);
                    if (targetPlayer.getUniqueId() != player.getUniqueId()) {
                        ComponentReplacer replacer = ComponentReplacer.create("target", targetPlayer.getName());
                        TeleportConfig.getInstance().getBackTeleportedSenderMessage().applyReplacer(replacer).sendMessage(player);
                    }
                } else {
                    TeleportConfig.getInstance().getLocationInvalidMessage().sendMessage(player);
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
            targetPlayer.teleportAsync(TeleportModule.getInstance().getLastLocation(targetPlayer)).thenAccept(success -> {
                if (success) {
                    TeleportConfig.getInstance().getBackTeleportedMessage().sendMessage(targetPlayer);

                    ComponentReplacer replacer = ComponentReplacer.create("target", targetPlayer.getName());
                    TeleportConfig.getInstance().getBackTeleportedSenderMessage().applyReplacer(replacer).sendMessage(console);
                } else {
                    TeleportConfig.getInstance().getLocationInvalidMessage().sendMessage(console);
                }
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
