package uk.firedev.firefly.modules.elevator.command;

import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import uk.firedev.daisylib.api.utils.ItemUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.firefly.modules.elevator.Elevator;
import uk.firedev.firefly.modules.elevator.ElevatorConfig;
import uk.firedev.firefly.modules.elevator.ElevatorModule;

import java.util.Objects;

public class ElevatorCommand {

    private ElevatorCommand() {}

    public static CommandTree getCommand() {
        return new CommandTree("elevator")
            .withPermission("firefly.command.elevator")
            .withShortDescription("Manage Elevators")
            .withFullDescription("Manage Elevators")
            .executes((sender, arguments) -> {
                ElevatorConfig.getInstance().getCommandUsageMessage().sendMessage(sender);
            })
            .then(getGiveBlockBranch())
            .then(getRemoveBranch());
    }

    private static Argument<String> getGiveBlockBranch() {
        return new LiteralArgument("giveBlock")
            .executesPlayer(info -> {
                Player player = info.sender();
                ItemUtils.giveItem(ElevatorModule.getInstance().getElevatorBlock(), player);
                ElevatorConfig.getInstance().getCommandGivenMessage().sendMessage(player);
            })
            .then(
                PlayerArgument.create("target")
                    .executes((sender, args) -> {
                        Player player = Objects.requireNonNull(args.getUnchecked("target"));
                        ItemUtils.giveItem(ElevatorModule.getInstance().getElevatorBlock(), player);
                        ElevatorConfig.getInstance().getCommandGivenMessage().sendMessage(player);
                    })
            );
    }

    private static Argument<String> getRemoveBranch() {
        return new LiteralArgument("remove")
            .executesPlayer((player, arguments) -> {
                RayTraceResult traced = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getEyeLocation().getDirection(), 5, FluidCollisionMode.NEVER, true);
                if (traced == null || traced.getHitBlock() == null) {
                    ElevatorConfig.getInstance().getCommandInvalidMessage().sendMessage(player);
                    return;
                }
                Elevator elevator = new Elevator(traced.getHitBlock());
                if (!elevator.isElevator()) {
                    ElevatorConfig.getInstance().getCommandInvalidMessage().sendMessage(player);
                    return;
                }
                elevator.setElevator(false);
                ElevatorConfig.getInstance().getCommandUnregisterMessage().sendMessage(player);
            });
    }

}
