package uk.firedev.firefly.modules.elevator.placeholders;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.utils.CommonUtils;
import uk.firedev.firefly.modules.elevator.Elevator;
import uk.firedev.firefly.modules.elevator.ElevatorModule;
import uk.firedev.firefly.placeholders.FireflyPlaceholder;

public class ElevatorLevelPlaceholder extends FireflyPlaceholder {

    public ElevatorLevelPlaceholder(@NonNull ElevatorModule module) {
        super(module);
    }

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.equals("elevator_level");
    }

    @Override
    public @Nullable String parse(@NonNull OfflinePlayer player) {
        Player online = player.getPlayer();
        if (online == null) {
            return null;
        }
        Block block = CommonUtils.getBelow(online.getLocation());
        Elevator elevator = new Elevator(block);
        if (!elevator.isElevator()) {
            return "N/A";
        }
        return String.valueOf(elevator.getCurrentPosition());
    }
}
