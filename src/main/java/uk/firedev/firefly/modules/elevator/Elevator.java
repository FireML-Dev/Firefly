package uk.firedev.firefly.modules.elevator;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.utils.ObjectUtils;
import uk.firedev.firefly.Firefly;
import uk.firedev.daisylib.libs.customblockdata.CustomBlockData;

import javax.annotation.Nullable;
import java.util.*;

public class Elevator {

    private static final Map<UUID, BossBar> bossBars = new HashMap<>();
    private static final Comparator<Elevator> comparator = Comparator.comparingInt(Elevator::getCurrentPosition);

    private final Location location;
    private final PersistentDataContainer pdc;

    public Elevator(@NotNull Location location) {
        this.location = location.getBlock().getLocation();
        this.pdc = location.getChunk().getPersistentDataContainer();
    }

    public Elevator(@NotNull Block block) {
        this.location = block.getLocation();
        this.pdc = block.getChunk().getPersistentDataContainer();
    }

    public NamespacedKey getStackKey() {
        return new NamespacedKey(Firefly.getInstance(), "elevator-" + location.getBlock().getX() + "_" + location.getBlock().getZ());
    }

    public Location getLocation() {
        return location;
    }

    public Location getTPLocation() {
        return location.toCenterLocation().add(0D, 0.5D, 0D);
    }

    public boolean isElevator() {
        return new CustomBlockData(location.getBlock(), Firefly.getInstance()).has(getStackKey());
    }

    public List<Elevator> getStack() {
        return CustomBlockData.getBlocksWithCustomData(Firefly.getInstance(), getLocation().getChunk()).stream()
            .filter(this::isInStack)
            .map(Elevator::new)
            .filter(Elevator::isElevator)
            .sorted(Comparator.comparing(elevator -> elevator.getLocation().getBlockY()))
            .toList();
    }

    private boolean isInStack(@NotNull Block block) {
        return location.getBlockX() == getLocation().getBlockX() && location.getBlockZ() == getLocation().getBlockZ();
    }

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

    public static void hideBossBar(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        BossBar bossBar = bossBars.get(uuid);
        if (bossBar == null) {
            return;
        }
        bossBars.remove(uuid);
        player.hideBossBar(bossBar);
    }

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

    public void setElevator(boolean shouldBeElevator) {
        CustomBlockData data = new CustomBlockData(location.getBlock(), Firefly.getInstance());
        if (shouldBeElevator) {
            data.set(getStackKey(), PersistentDataType.BOOLEAN, true);
        } else {
            data.remove(getStackKey());
        }
    }

    /**
     * @return The current position of this elevator, or -1 if the elevator is not in the stack.
     */
    public int getCurrentPosition() {
        return getStack().indexOf(this);
    }

    @Nullable
    public Elevator getNext() {
        int index = getCurrentPosition() + 1;
        return ObjectUtils.getOrDefault(getStack(), index, null);
    }

    @Nullable
    public Elevator getPrevious() {
        int index = getCurrentPosition() - 1;
        return ObjectUtils.getOrDefault(getStack(), index, null);
    }

    public void handleBossBar(@NotNull Player player) {
        if (isElevator()) {
            showBossBar(player);
        } else {
            hideBossBar(player);
        }
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
        return this.location.equals(other.location);
    }

}
