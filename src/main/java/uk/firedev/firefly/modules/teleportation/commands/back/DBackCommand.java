package uk.firedev.firefly.modules.teleportation.commands.back;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;
import uk.firedev.firefly.utils.TeleportWarmup;

public class DBackCommand {

    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("dback")
            .requires(stack -> stack.getSender().hasPermission("firefly.command.back"))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                teleportPlayer(player, player);
                return 1;
            })
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .executes(context -> {
                        Player target = context.getArgument("target", Player.class);
                        teleportPlayer(context.getSource().getSender(), target);
                        return 1;
                    })
            )
            .build();
    }

    private static void teleportPlayer(@NotNull CommandSender sender, @NotNull Player target) {
        Location lastDeath = target.getLastDeathLocation();
        if (lastDeath == null) {
            TeleportConfig.getInstance().getLocationInvalidMessage().send(sender);
            return;
        }
        new TeleportWarmup(target, lastDeath, TeleportConfig.getInstance().getBackWarmupSeconds())
            .withSuccessMessage(() -> {
                if (sender instanceof Player player && player.equals(target)) {
                    return TeleportConfig.getInstance().getDBackTeleportedMessage();
                } else {
                    return TeleportConfig.getInstance().getDBackTeleportedSenderMessage()
                        .replace("{target}", target.getName());
                }
            })
            .start();
    }
    
}
