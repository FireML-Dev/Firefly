package uk.firedev.skylight.modules.elevator;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.skylight.Skylight;

import javax.annotation.Nullable;
import java.util.*;

public class Elevator {

    private static final Map<Audience, BossBar> bossBars = new HashMap<>();

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
        return ObjectUtils.createNamespacedKey("elevator-" + location.getBlock().getX() + "_" + location.getBlock().getZ(), Skylight.getInstance());
    }

    public NamespacedKey getMaterialKey() {
        return ObjectUtils.createNamespacedKey("elevator-material", Skylight.getInstance());
    }

    public Location getLocation() {
        return location;
    }

    public Location getTPLocation() { return location.toCenterLocation().add(0D, 0.5D, 0D); }

    public boolean isElevator() {
        return getStack().contains(String.valueOf(location.getBlock().getY()));
    }

    public List<String> getStack() {
        try {
            List<String> stackList = new ArrayList<>(this.pdc.getOrDefault(getStackKey(), PersistentDataType.LIST.strings(), List.of()));
            stackList.sort(Comparator.comparingInt(Integer::parseInt));
            return stackList;
        } catch (IllegalArgumentException ex) {
            this.pdc.remove(getStackKey());
            return List.of();
        }
    }

    public void showBossBar(@NotNull Player player) {
        BossBar bossBar = ElevatorConfig.getInstance().getBossBar(this);
        BossBar existing = bossBars.get(player);
        if (existing != null) {
            bossBars.remove(player);
            player.hideBossBar(existing);
        }
        bossBars.put(player, bossBar);
        player.showBossBar(bossBar);
    }

    public static void hideBossBar(@NotNull Player player) {
        BossBar bossBar = bossBars.get(player);
        if (bossBar == null) {
            return;
        }
        bossBars.remove(player);
        player.hideBossBar(bossBar);
    }

    public static void hideAllBossBars() {
        bossBars.forEach(Audience::hideBossBar);
        bossBars.clear();
    }

    public void setElevator(boolean isElevator) {
        List<String> stack = new ArrayList<>(getStack());
        String y = String.valueOf(location.getBlock().getY());
        if (isElevator) {
            if (!stack.contains(y)) {
                stack.add(y);
            }
        } else {
            stack.remove(y);
        }
        this.pdc.set(getStackKey(), PersistentDataType.LIST.strings(), stack);
    }

    public int getCurrentPosition() {
        List<String> stack = getStack();
        return stack.indexOf(String.valueOf(location.getBlock().getY()));
    }

    @Nullable
    public Elevator getNext() {
        List<String> stack = getStack();
        int currentPos = getCurrentPosition();
        if (currentPos == -1) {
            return null;
        }
        String nextPos;
        try {
            nextPos = stack.get(currentPos + 1);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        if (!ObjectUtils.isDouble(nextPos)) {
            return null;
        }
        Location location = this.location.getBlock().getLocation();
        location.setY(Double.parseDouble(nextPos));
        return new Elevator(location);
    }

    @Nullable
    public Elevator getPrevious() {
        List<String> stack = getStack();
        int currentPos = getCurrentPosition();
        if (currentPos == -1) {
            return null;
        }
        String previousPos;
        try {
            previousPos = stack.get(currentPos - 1);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        if (!ObjectUtils.isDouble(previousPos)) {
            return null;
        }
        Location location = new Location(this.location.getBlock().getWorld(), this.location.getBlock().getX(), Double.parseDouble(previousPos), this.location.getBlock().getZ());
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
