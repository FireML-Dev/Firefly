package uk.firedev.firefly.modules.small;

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
import uk.firedev.firefly.Module;

import java.util.List;
import java.util.Objects;

public class LootChestProtection implements Listener, Module {

    private static LootChestProtection instance = null;

    private boolean loaded = false;

    private LootChestProtection() {}

    public static LootChestProtection getInstance() {
        if (instance == null) {
            instance = new LootChestProtection();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "LootChestProtection";
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, Firefly.getInstance());
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        // There is nothing to reload here :)
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        // Unregister the event listener
        HandlerList.unregisterAll(this);
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Lootable lootable && lootable.getLootTable() != null && getProtectedWorlds().contains(block.getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDestroy(VehicleDestroyEvent event) {
        Entity entity = event.getVehicle();
        if (entity instanceof Lootable lootable && lootable.getLootTable() != null && getProtectedWorlds().contains(entity.getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplodes(BlockExplodeEvent event) {
        event.blockList().removeIf(
                block -> block.getState() instanceof Lootable lootable && lootable.getLootTable() != null && getProtectedWorlds().contains(block.getWorld())
        );
    }

    @EventHandler
    public void onEntityExplodes(EntityExplodeEvent event) {
        event.blockList().removeIf(
                block -> block.getState() instanceof Lootable lootable && lootable.getLootTable() != null && getProtectedWorlds().contains(block.getWorld())
        );
    }

    private List<World> getProtectedWorlds() {
        return SmallConfig.getInstance().getConfig().getStringList("loot-chest-protection.settings.protected-worlds").stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .toList();
    }

}
