package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Manager;
import uk.firedev.firefly.modules.teleportation.commands.SetFirstSpawnCommand;
import uk.firedev.firefly.modules.teleportation.commands.SetSpawnCommand;
import uk.firedev.firefly.modules.teleportation.commands.SpawnCommand;

public class TeleportManager implements Manager {

    private static TeleportManager instance;

    private boolean loaded;
    private Location spawnLocation;
    private Location firstSpawnLocation;

    private TeleportManager() {}

    public static TeleportManager getInstance() {
        if (instance == null) {
            instance = new TeleportManager();
        }
        return instance;
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        TeleportConfig.getInstance().reload();
        refreshSpawnLocations();
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Location Commands");
        SpawnCommand.getInstance().register();
        SetSpawnCommand.getInstance().register();
        SetFirstSpawnCommand.getInstance().register();
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public void refreshSpawnLocations() {
        spawnLocation = TeleportConfig.getInstance().getSpawnLocation(false);
        firstSpawnLocation = TeleportConfig.getInstance().getSpawnLocation(true);
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Location getFirstSpawnLocation() {
        return firstSpawnLocation;
    }

    public boolean sendToSpawn(boolean firstSpawn, @NotNull Player player) {
        Location location = firstSpawn ? getFirstSpawnLocation() : getSpawnLocation();
        if (location == null) {
            // TODO send spawn missing message - warn admins
            return false;
        }
        player.teleportAsync(location);
        // TODO send teleported to spawn message to target
        return true;
    }

    public void sendToSpawn(boolean firstSpawn, @NotNull Player player, @Nullable CommandSender sender) {
        if (sendToSpawn(firstSpawn, player)) {
            // TODO send sent to spawn message
        }
    }

}
