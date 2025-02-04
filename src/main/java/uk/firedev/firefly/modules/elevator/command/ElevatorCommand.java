package uk.firedev.firefly.modules.elevator.command;

import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import uk.firedev.daisylib.api.utils.ItemUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.firefly.modules.elevator.Elevator;
import uk.firedev.firefly.modules.elevator.ElevatorConfig;
import uk.firedev.firefly.modules.elevator.ElevatorModule;

public class ElevatorCommand {

    private static CommandAPICommand command;

    private ElevatorCommand() {}

    public static CommandAPICommand getCommand() {
        if (command == null) {
            command = new CommandAPICommand("elevator")
                .withPermission("firefly.command.elevator")
                .withShortDescription("Manage Elevators")
                .withFullDescription("Manage Elevators")
                .withSubcommands(getGiveBlockCommand(), getUnsetElevatorCommand())
                .executes((sender, arguments) -> {
                    ElevatorConfig.getInstance().getCommandUsageMessage().sendMessage(sender);
                });
        }
        return command;
    }

    private static CommandAPICommand getGiveBlockCommand() {
        return new CommandAPICommand("giveBlock")
                .withArguments(PlayerArgument.create("target").setOptional(true))
                .executesPlayer((player, arguments) -> {
                    Player target = arguments.getUnchecked("target");
                    if (target == null) {
                        target = player;
                    }
                    ItemUtils.giveItem(ElevatorModule.getInstance().getElevatorBlock(), target);
                    ElevatorConfig.getInstance().getCommandGivenMessage().sendMessage(target);
                });
    }

    private static CommandAPICommand getUnsetElevatorCommand() {
        return new CommandAPICommand("remove")
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
