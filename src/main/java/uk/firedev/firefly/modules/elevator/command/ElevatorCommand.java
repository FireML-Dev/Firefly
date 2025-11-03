package uk.firedev.firefly.modules.elevator.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.firefly.modules.elevator.Elevator;
import uk.firedev.firefly.modules.elevator.ElevatorConfig;
import uk.firedev.firefly.modules.elevator.ElevatorModule;

import java.util.Objects;

public class ElevatorCommand {

    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("elevator")
            .requires(stack -> ElevatorModule.getInstance().isConfigEnabled() &&  stack.getSender().hasPermission("firefly.command.elevator"))
            .executes(context -> {
                ElevatorConfig.getInstance().getCommandUsageMessage().send(context.getSource().getSender());
                return 1;
            })
            .then(giveBlock())
            .then(remove())
            .build();
    }

    private ArgumentBuilder<CommandSourceStack, ?> giveBlock() {
        return Commands.literal("giveBlock")
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                ItemUtils.giveItem(ElevatorModule.getInstance().getElevatorBlock(), player);
                ElevatorConfig.getInstance().getCommandGivenMessage().send(player);
                return 1;
            })
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .executes(context -> {
                        Player player = context.getArgument("target", Player.class);
                        ItemUtils.giveItem(ElevatorModule.getInstance().getElevatorBlock(), player);
                        ElevatorConfig.getInstance().getCommandGivenMessage().send(player);
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> remove() {
        return Commands.literal("remove")
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                RayTraceResult traced = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getEyeLocation().getDirection(), 5, FluidCollisionMode.NEVER, true);
                if (traced == null || traced.getHitBlock() == null) {
                    ElevatorConfig.getInstance().getCommandInvalidMessage().send(player);
                    return 1;
                }
                Elevator elevator = new Elevator(traced.getHitBlock());
                if (!elevator.isElevator()) {
                    ElevatorConfig.getInstance().getCommandInvalidMessage().send(player);
                    return 1;
                }
                elevator.setElevator(false);
                ElevatorConfig.getInstance().getCommandUnregisterMessage().send(player);
                return 1;
            });
    }

}
