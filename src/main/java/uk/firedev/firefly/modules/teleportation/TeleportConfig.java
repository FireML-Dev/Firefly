package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.Loggers;
import uk.firedev.firefly.Firefly;

import java.io.IOException;

public class TeleportConfig extends Config {

    private static TeleportConfig instance;

    private TeleportConfig() {
        super("modules/teleport.yml", "modules/teleport.yml", Firefly.getInstance(), true);
    }

    public static TeleportConfig getInstance() {
        if (instance == null) {
            instance = new TeleportConfig();
        }
        return instance;
    }

    // Spawn command related things

    public void setSpawnLocation(boolean firstSpawn, @NotNull Location location) {
        String spawnLocKey = firstSpawn ? "first-spawn-location" : "spawn-location";
        getConfig().set(spawnLocKey + ".world", location.getWorld().getName());
        getConfig().set(spawnLocKey + ".x", location.getX());
        getConfig().set(spawnLocKey + ".y", location.getY());
        getConfig().set(spawnLocKey + ".z", location.getZ());
        getConfig().set(spawnLocKey + ".yaw", location.getYaw());
        getConfig().set(spawnLocKey + ".pitch", location.getPitch());
        try {
            getConfig().save();
            TeleportManager.getInstance().refreshSpawnLocations();
        } catch (IOException exception) {
            Loggers.warn(Firefly.getInstance().getComponentLogger(), "Failed to save spawn location to config!", exception);
        }
    }

    public Location getSpawnLocation(boolean firstSpawn) {
        String spawnLocKey = firstSpawn ? "first-spawn-location" : "spawn-location";
        String worldName = getConfig().getString(spawnLocKey + ".world");
        double x = getConfig().getDouble(spawnLocKey + ".x");
        double y = getConfig().getDouble(spawnLocKey + ".y");
        double z = getConfig().getDouble(spawnLocKey + ".z");
        float yaw = getConfig().getFloat(spawnLocKey + ".yaw");
        float pitch = getConfig().getFloat(spawnLocKey + ".pitch");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new RuntimeException("The world linked to the spawn location is not valid!");
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

}
