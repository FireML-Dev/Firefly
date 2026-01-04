package uk.firedev.firefly.modules.elevator.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.util.Utils;
import uk.firedev.daisylib.command.argument.PlayerArgument;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.modules.elevator.Elevator;
import uk.firedev.firefly.modules.elevator.ElevatorConfig;
import uk.firedev.firefly.modules.elevator.ElevatorModule;

import java.util.List;
import java.util.Objects;

public class ElevatorCommand implements CommandHolder {

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("elevator")
            .requires(stack -> ElevatorModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission(permission()))
            .executes(context -> {
                ElevatorConfig.getInstance().getCommandUsageMessage().send(context.getSource().getSender());
                return 1;
            })
            .then(giveBlock())
            .then(remove())
            .build();
    }

    /**
     * @return The list of aliases this command should have.
     */
    @NotNull
    @Override
    public List<String> aliases() {
        return List.of();
    }

    /**
     * @return The permission for executing this command on yourself.
     */
    @NotNull
    @Override
    public String permission() {
        return "firefly.command.elevator";
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @NotNull
    @Override
    public String targetPermission() {
        return "firefly.command.elevator";
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    public String description() {
        return null;
    }

    private ArgumentBuilder<CommandSourceStack, ?> giveBlock() {
        return Commands.literal("giveBlock")
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                player.give(ElevatorModule.getInstance().getElevatorBlock());
                ElevatorConfig.getInstance().getCommandGivenMessage().send(player);
                return 1;
            })
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .executes(context -> {
                        Player player = context.getArgument("target", Player.class);
                        player.give(ElevatorModule.getInstance().getElevatorBlock());
                        ElevatorConfig.getInstance().getCommandGivenMessage().send(player);
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> remove() {
        return Commands.literal("remove")
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
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
