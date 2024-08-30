package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Manager;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.modules.playtime.PlaytimeDatabase;
import uk.firedev.firefly.modules.teleportation.commands.BackCommand;
import uk.firedev.firefly.modules.teleportation.commands.SetFirstSpawnCommand;
import uk.firedev.firefly.modules.teleportation.commands.SetSpawnCommand;
import uk.firedev.firefly.modules.teleportation.commands.SpawnCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager implements Manager {

    private static TeleportManager instance;

    private boolean loaded;
    private Location spawnLocation;
    private Location firstSpawnLocation;
    private Map<UUID, Location> playerLastLocationMap;

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
        TeleportDatabase.getInstance().register(Database.getInstance());
        refreshSpawnLocations();
        Bukkit.getPluginManager().registerEvents(new TeleportListener(), Firefly.getInstance());
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Location Commands");
        SpawnCommand.getInstance().register();
        SetSpawnCommand.getInstance().register();
        SetFirstSpawnCommand.getInstance().register();
        BackCommand.getInstance().register();
        populateLastLocationMap();
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        TeleportConfig.getInstance().reload();
        populateLastLocationMap();
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        saveAllLastLocations();
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    // /spawn

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
        String invalid = firstSpawn ? "The first spawn location is not valid!" : "The spawn location is not valid!";
        if (location == null) {
            // TODO proper message
            player.sendMessage("Spawn location was invalid");
            Loggers.warn(Firefly.getInstance().getComponentLogger(), invalid);
            return false;
        }
        player.teleportAsync(location);
        // TODO proper message
        player.sendMessage("Sent to spawn.");
        return true;
    }

    public void sendToSpawn(boolean firstSpawn, @NotNull Player player, @Nullable CommandSender sender) {
        if (sendToSpawn(firstSpawn, player)) {
            // TODO proper messages
            player.sendMessage("Sent to spawn.");
            sender.sendMessage("Sent player to spawn.");
        }
    }

    // /back

    public void populateLastLocationMap() {
        playerLastLocationMap = new HashMap<>(TeleportDatabase.getInstance().getLastLocations());
    }

    public Map<UUID, Location> getPlayerLastLocationMap() {
        if (playerLastLocationMap == null) {
            playerLastLocationMap = new HashMap<>();
        }
        return Map.copyOf(playerLastLocationMap);
    }

    private Map<UUID, Location> getEditablePlayerLastLocationMap() {
        if (playerLastLocationMap == null) {
            playerLastLocationMap = new HashMap<>();
        }
        return playerLastLocationMap;
    }

    public void setLastLocation(@NotNull HumanEntity humanEntity, @NotNull Location location) {
        getEditablePlayerLastLocationMap().put(humanEntity.getUniqueId(), location);
    }

    public Location getLastLocation(@NotNull HumanEntity humanEntity) {
        return getEditablePlayerLastLocationMap().computeIfAbsent(humanEntity.getUniqueId(), handling -> humanEntity.getLocation());
    }

    public void saveAllLastLocations() {
        getPlayerLastLocationMap().forEach(TeleportDatabase.getInstance()::saveLastLocationToDatabase);
    }

}
