package uk.firedev.firefly.modules.command.commands.workstations;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.firefly.modules.command.Command;

public interface WorkstationCommand extends Command {

    @Override
    default @NotNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal(getCommandName())
            .requires(stack -> stack.getSender().hasPermission(getPermission()))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                open(player);
                return 1;
            })
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .requires(stack -> stack.getSender().hasPermission(getTargetPermission()))
                    .executes(context -> {
                        Player player = context.getArgument("target", Player.class);
                        open(player);
                        return 1;
                    })
            )
            .build();
    }

    void open(@NotNull Player player);

}
