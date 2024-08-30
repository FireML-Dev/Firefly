package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Location;
import uk.firedev.daisylib.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Manager;
import uk.firedev.firefly.modules.teleportation.commands.SetFirstSpawnCommand;
import uk.firedev.firefly.modules.teleportation.commands.SetSpawnCommand;

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

}
