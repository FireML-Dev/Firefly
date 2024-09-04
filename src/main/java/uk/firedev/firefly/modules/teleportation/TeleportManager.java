package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Fire;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Manager;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.modules.teleportation.commands.back.BackCommand;
import uk.firedev.firefly.modules.teleportation.commands.back.DBackCommand;
import uk.firedev.firefly.modules.teleportation.commands.spawn.SetFirstSpawnCommand;
import uk.firedev.firefly.modules.teleportation.commands.spawn.SetSpawnCommand;
import uk.firedev.firefly.modules.teleportation.commands.spawn.SpawnCommand;
import uk.firedev.firefly.modules.teleportation.commands.tpa.TPACommand;
import uk.firedev.firefly.modules.teleportation.commands.tpa.TPAHereCommand;
import uk.firedev.firefly.modules.teleportation.commands.tpa.TPAcceptCommand;
import uk.firedev.firefly.modules.teleportation.commands.tpa.TPDenyCommand;

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

        // Command Registering
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Location Commands");

        SpawnCommand.getInstance().register(Firefly.getInstance());
        SetSpawnCommand.getInstance().register(Firefly.getInstance());
        SetFirstSpawnCommand.getInstance().register(Firefly.getInstance());

        BackCommand.getInstance().register(Firefly.getInstance());
        DBackCommand.getInstance().register(Firefly.getInstance());

        TPACommand.getInstance().register(Firefly.getInstance());
        TPAHereCommand.getInstance().register(Firefly.getInstance());
        TPDenyCommand.getInstance().register(Firefly.getInstance());
        TPAcceptCommand.getInstance().register(Firefly.getInstance());
        // Command Registering

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

    public boolean sendToSpawn(boolean firstSpawn, @NotNull Player player, boolean sendMessage) {
        Location location = firstSpawn ? getFirstSpawnLocation() : getSpawnLocation();
        String invalid = firstSpawn ? "The first spawn location is not valid!" : "The spawn location is not valid!";
        if (location == null) {
            // TODO send invalid location message
            if (sendMessage) {
                player.sendMessage("Spawn location was invalid");
            }
            Loggers.warn(Firefly.getInstance().getComponentLogger(), invalid);
            return false;
        }
        player.teleportAsync(location);
        if (sendMessage) {
            // TODO send sent to spawn message
            player.sendMessage("Sent to spawn.");
        }
        return true;
    }

    public void sendToSpawn(boolean firstSpawn, @NotNull Player player, @Nullable CommandSender sender, boolean sendMessage) {
        if (sendToSpawn(firstSpawn, player, sendMessage) && sender != null) {
            // TODO send sent to spawn message
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
