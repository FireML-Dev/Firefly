package uk.firedev.firefly.modules.elevator;

import net.kyori.adventure.bossbar.BossBar;
import uk.firedev.daisylib.util.Utils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.customblockdata.CustomBlockData;
import uk.firedev.firefly.Firefly;

import javax.annotation.Nullable;
import java.util.*;

public class Elevator {

    private static final Map<UUID, BossBar> bossBars = new HashMap<>();
    private static final NamespacedKey elevatorKey = new NamespacedKey(Firefly.getInstance(), "elevator");

    private final Block block;

    public Elevator(@NotNull Location location) {
        this.block = location.getBlock();
    }

    public Elevator(@NotNull Block block) {
        this.block = block;
    }

    /**
     * @return The block of this elevator
     */
    public Block getBlock() {
        return block;
    }

    /**
     * @return The location to teleport players to
     */
    public Location getTPLocation() {
        return block.getLocation().toCenterLocation().add(0D, 0.5D, 0D);
    }

    /**
     * @return Is this an elevator?
     */
    public boolean isElevator() {
        return new CustomBlockData(block, Firefly.getInstance()).has(elevatorKey);
    }

    /**
     * Gets this elevator's stack
     * @return This elevator's stack, or an empty list if this is not an elevator.
     */
    public List<Elevator> getStack() {
        if (!isElevator()) {
            return List.of();
        }
        return CustomBlockData.getBlocksWithCustomData(Firefly.getInstance(), getBlock().getChunk()).stream()
            .filter(this::isInStack)
            .map(Elevator::new)
            .filter(Elevator::isElevator)
            .sorted(Comparator.comparing(elevator -> elevator.getBlock().getY()))
            .toList();
    }

    /**
     * @param block The block to check
     * @return Checks if the provided block is in the same stack as this elevator
     */
    private boolean isInStack(@NotNull Block block) {
        return this.block.getX() == block.getX() && this.block.getZ() == block.getZ();
    }

    /**
     * Shows the elevator bossbar
     * @param player The player to show the bossbar to
     */
    public void showBossBar(@NotNull Player player) {
        BossBar bossBar = ElevatorConfig.getInstance().getBossBar(this);
        UUID uuid = player.getUniqueId();
        BossBar existing = bossBars.get(uuid);
        if (existing != null) {
            bossBars.remove(uuid);
            player.hideBossBar(existing);
        }
        bossBars.put(uuid, bossBar);
        player.showBossBar(bossBar);
    }

    /**
     * Hides the elevator bossbar
     * @param player The player to hide the bossbar from
     */
    public static void hideBossBar(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        BossBar bossBar = bossBars.remove(uuid);
        if (bossBar == null) {
            return;
        }
        player.hideBossBar(bossBar);
    }

    /**
     * Hides all elevator bossbars
     */
    public static void hideAllBossBars() {
        Iterator<BossBar> barIterator = bossBars.values().iterator();
        while (barIterator.hasNext()) {
            BossBar bar = barIterator.next();
            bar.viewers().forEach(viewer -> {
                if (!(viewer instanceof Player player)) {
                    return;
                }
                player.hideBossBar(bar);
            });
            barIterator.remove();
        }
    }

    /**
     * Sets whether this block is an elevator
     */
    public void setElevator(boolean shouldBeElevator) {
        CustomBlockData data = new CustomBlockData(block, Firefly.getInstance());
        if (shouldBeElevator) {
            data.set(elevatorKey, PersistentDataType.BOOLEAN, true);
        } else {
            data.remove(elevatorKey);
        }
    }

    /**
     * @return The current position of this elevator, or -1 if the elevator is not in the stack.
     */
    public int getCurrentPosition() {
        return getStack().indexOf(this);
    }

    /**
     * @return The next elevator in the stack, or null if this is the last elevator.
     */
    @Nullable
    public Elevator getNext() {
        int index = getCurrentPosition() + 1;
        return Utils.getOrDefault(getStack(), index, null);
    }

    /**
     * @return The previous elevator in the stack, or null if this is the first elevator.
     */
    @Nullable
    public Elevator getPrevious() {
        int index = getCurrentPosition() - 1;
        return Utils.getOrDefault(getStack(), index, null);
    }

    /**
     * Shows or hides the bossbar for this elevator
     */
    public void handleBossBar(@NotNull Player player) {
        if (isElevator()) {
            showBossBar(player);
        } else {
            hideBossBar(player);
        }
    }

    @Override
    public int hashCode() {
        return block.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Elevator other = (Elevator) obj;
        return this.block.equals(other.block);
    }

}
