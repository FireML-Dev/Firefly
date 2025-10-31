package uk.firedev.firefly.modules.protection.protections;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.loot.Lootable;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.modules.protection.ProtectionConfig;

import java.util.List;
import java.util.Objects;

public class LootChestProtection implements SubModule, Listener {

    private static LootChestProtection instance = null;

    private LootChestProtection() {}

    public static LootChestProtection getInstance() {
        if (instance == null) {
            instance = new LootChestProtection();
        }
        return instance;
    }

    @Override
    public boolean isConfigEnabled() {
        return ProtectionConfig.getInstance().isLootChestProtectionEnabled();
    }

    @Override
    public void init() {}

    @Override
    public void reload() {}

    @Override
    public void unload() {}

    @Override
    public boolean isLoaded() {
        return true;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!isConfigEnabled()) {
            return;
        }
        Block block = event.getBlock();
        if (block.getState() instanceof Lootable lootable && lootable.getLootTable() != null && getProtectedWorlds().contains(block.getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDestroy(VehicleDestroyEvent event) {
        if (!isConfigEnabled()) {
            return;
        }
        Entity entity = event.getVehicle();
        if (entity instanceof Lootable lootable && lootable.getLootTable() != null && getProtectedWorlds().contains(entity.getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplodes(BlockExplodeEvent event) {
        if (!isConfigEnabled()) {
            return;
        }
        event.blockList().removeIf(
                block -> block.getState() instanceof Lootable lootable && lootable.getLootTable() != null && getProtectedWorlds().contains(block.getWorld())
        );
    }

    @EventHandler
    public void onEntityExplodes(EntityExplodeEvent event) {
        if (!isConfigEnabled()) {
            return;
        }
        event.blockList().removeIf(
                block -> block.getState() instanceof Lootable lootable && lootable.getLootTable() != null && getProtectedWorlds().contains(block.getWorld())
        );
    }

    private List<World> getProtectedWorlds() {
        return ProtectionConfig.getInstance().getConfig().getStringList("loot-chest-protection.settings.protected-worlds").stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .toList();
    }

}
