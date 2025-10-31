package uk.firedev.firefly.modules.teleportation.commands.tpa;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;
import uk.firedev.firefly.modules.teleportation.tpa.TPARequest;

public class TPACommand {

    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("tpa")
            .requires(stack -> stack.getSender().hasPermission("firefly.command.tpa"))
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .executes(context -> {
                        Player player = CommandUtils.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        Player target = context.getArgument("target", Player.class);
                        TPAHandler.getInstance().sendRequest(target, player, TPARequest.TPADirection.SENDER_TO_TARGET);
                        return 1;
                    })
            )
            .build();
    }

}
