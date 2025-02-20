package uk.firedev.firefly.modules.elevator;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBarViewer;
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

import javax.annotation.Nullable;
import java.util.*;

public class Elevator {

    private static final Map<UUID, BossBar> bossBars = new HashMap<>();

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
        return getStack().contains(location.getBlock().getY());
    }

    public List<Integer> getStack() {
        List<Integer> stack = this.pdc.getOrDefault(getStackKey(), PersistentDataType.LIST.integers(), List.of());
        return stack.stream()
            .sorted()
            .toList();
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

    public void setElevator(boolean isElevator) {
        List<Integer> stack = new ArrayList<>(getStack());
        int y = location.getBlock().getY();
        if (isElevator) {
            if (!stack.contains(y)) {
                stack.add(y);
            }
        } else {
            stack.remove(y);
        }
        this.pdc.set(getStackKey(), PersistentDataType.LIST.integers(), stack);
    }

    public int getCurrentPosition() {
        List<Integer> stack = getStack();
        int index = stack.indexOf(location.getBlock().getY());
        if (index == -1) {
            return index;
        }
        return index + 1;
    }

    @Nullable
    public Elevator getNext() {
        List<Integer> stack = getStack();
        int currentPos = getCurrentPosition();
        if (currentPos == -1) {
            return null;
        }
        int nextPos;
        try {
            nextPos = stack.get(currentPos + 1);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        Location location = this.location.clone();
        location.setY(nextPos);
        return new Elevator(location);
    }

    @Nullable
    public Elevator getPrevious() {
        List<Integer> stack = getStack();
        int currentPos = getCurrentPosition();
        if (currentPos == -1) {
            return null;
        }
        int previousPos;
        try {
            previousPos = stack.get(currentPos - 1);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        Location location = this.location.clone();
        location.setY(previousPos);
        return new Elevator(location);
    }

    public void handleBossBar(@NotNull Player player) {
        if (isElevator()) {
            showBossBar(player);
        } else {
            hideBossBar(player);
        }
    }

}
