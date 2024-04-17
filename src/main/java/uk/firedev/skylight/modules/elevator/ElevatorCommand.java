package uk.firedev.skylight.modules.elevator;

import org.bukkit.FluidCollisionMode;
import org.bukkit.util.RayTraceResult;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.skylight.config.MessageConfig;

public class ElevatorCommand extends CommandAPICommand {

    private static ElevatorCommand instance = null;

    private ElevatorCommand() {
        super("elevator");
        withPermission("skylight.command.elevator");
        withShortDescription("Manage Elevators");
        withFullDescription("Manage Elevators");
        withSubcommands(getGiveBlockCommand(), getUnsetElevatorCommand());
        executes((sender, arguments) -> {
            MessageConfig.getInstance().sendPrefixedMessageFromConfig(sender, "messages.elevator.command.usage");
        });
    }

    public static ElevatorCommand getInstance() {
        if (instance == null) {
            instance = new ElevatorCommand();
        }
        return instance;
    }

    private CommandAPICommand getGiveBlockCommand() {
        return new CommandAPICommand("giveBlock")
                .executesPlayer((player, arguments) -> {
                    ItemUtils.giveItem(ElevatorManager.getInstance().getElevatorBlock(), player);
                    MessageConfig.getInstance().sendMessageFromConfig(player, "messages.elevator.command.block-given");
                });
    }

    private CommandAPICommand getUnsetElevatorCommand() {
        return new CommandAPICommand("unsetElevator")
                .executesPlayer((player, arguments) -> {
                    RayTraceResult traced = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getEyeLocation().getDirection(), 5, FluidCollisionMode.NEVER, true);
                    if (traced == null || traced.getHitBlock() == null) {
                        MessageConfig.getInstance().sendMessageFromConfig(player, "messages.elevator.command.not-an-elevator");
                        return;
                    }
                    Elevator elevator = new Elevator(traced.getHitBlock());
                    if (!elevator.isElevator()) {
                        MessageConfig.getInstance().sendMessageFromConfig(player, "messages.elevator.command.not-an-elevator");
                        return;
                    }
                    elevator.setElevator(false);
                    MessageConfig.getInstance().sendMessageFromConfig(player, "messages.elevator.command.unregistered-elevator");
                });
    }

}