package uk.firedev.firefly.modules.teleportation.commands.tpa;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.firefly.modules.teleportation.TeleportModule;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;

public class TPDenyCommand {

    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("tpdeny")
            .requires(stack -> TeleportModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission("firefly.command.tpa"))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                TPAHandler.getInstance().denyRequest(player);
                return 1;
            })
            .build();
    }

}
