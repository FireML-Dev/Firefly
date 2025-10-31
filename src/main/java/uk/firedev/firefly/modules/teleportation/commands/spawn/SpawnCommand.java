package uk.firedev.firefly.modules.teleportation.commands.spawn;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.firefly.modules.teleportation.TeleportModule;

import java.util.Objects;

public class SpawnCommand {

    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("spawn")
            .requires(stack -> stack.getSender().hasPermission("firefly.command.spawn"))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                TeleportModule.getInstance().sendToSpawn(false, player, true);
                return 1;
            })
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .requires(stack -> stack.getSender().hasPermission("firefly.command.spawn.other"))
                    .executes(context -> {
                        Player target = context.getArgument("target", Player.class);
                        TeleportModule.getInstance().sendToSpawn(false, target, context.getSource().getSender(), true);
                        return 1;
                    })
            )
            .build();
    }

}
