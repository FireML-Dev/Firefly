package uk.firedev.skylight.elevator;

import org.bukkit.FluidCollisionMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.command.ICommand;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.skylight.config.MessageConfig;

import java.util.List;

public class ElevatorCommand implements ICommand {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        switch (args.length) {
            case 1 -> {
                switch (args[0]) {
                    case "giveBlock" -> {
                        ItemUtils.giveItem(ElevatorManager.getInstance().getElevatorBlock(), player);
                        MessageConfig.getInstance().sendMessageFromConfig(player, "messages.elevator.block-given");
                        return true;
                    }
                    case "unsetElevator" -> {
                        RayTraceResult traced = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getEyeLocation().getDirection(), 5, FluidCollisionMode.NEVER, true);
                        if (traced == null || traced.getHitBlock() == null) {
                            MessageConfig.getInstance().sendMessageFromConfig(player, "messages.elevator.not-an-elevator");
                            return true;
                        }
                        Elevator elevator = new Elevator(traced.getHitBlock());
                        if (!elevator.isElevator()) {
                            MessageConfig.getInstance().sendMessageFromConfig(player, "messages.elevator.not-an-elevator");
                            return true;
                        }
                        elevator.setElevator(false);
                        MessageConfig.getInstance().sendMessageFromConfig(player, "messages.elevator.unregistered-elevator");
                        return true;
                    }
                }
            }
            default -> {
                return false;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return List.of();
        }
        return switch (args.length) {
            case 1 -> processTabCompletions(args[0], List.of("giveBlock"));
            default -> List.of();
        };
    }

}
