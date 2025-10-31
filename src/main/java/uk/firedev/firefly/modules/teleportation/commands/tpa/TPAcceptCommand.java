package uk.firedev.firefly.modules.teleportation.commands.tpa;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;

public class TPAcceptCommand {

    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("tpaccept")
            .requires(stack -> stack.getSender().hasPermission("firefly.command.tpa"))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                TPAHandler.getInstance().acceptRequest(player);
                return 1;
            })
            .build();
    }

}
