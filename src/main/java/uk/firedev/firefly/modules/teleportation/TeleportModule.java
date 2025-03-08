package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.teleportation.commands.back.BackCommand;
import uk.firedev.firefly.modules.teleportation.commands.back.DBackCommand;
import uk.firedev.firefly.modules.teleportation.commands.spawn.SetFirstSpawnCommand;
import uk.firedev.firefly.modules.teleportation.commands.spawn.SetSpawnCommand;
import uk.firedev.firefly.modules.teleportation.commands.spawn.SpawnCommand;
import uk.firedev.firefly.modules.teleportation.commands.tpa.TPACommand;
import uk.firedev.firefly.modules.teleportation.commands.tpa.TPAHereCommand;
import uk.firedev.firefly.modules.teleportation.commands.tpa.TPAcceptCommand;
import uk.firedev.firefly.modules.teleportation.commands.tpa.TPDenyCommand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportModule implements Module {

    private static TeleportModule instance;

    private boolean loaded;
    private Location spawnLocation;
    private Location firstSpawnLocation;
    private final Map<UUID, Location> playerLastLocationMap;

    private TeleportModule() {
        playerLastLocationMap = new ConcurrentHashMap<>();
    }

    public static TeleportModule getInstance() {
        if (instance == null) {
            instance = new TeleportModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "Teleportation";
    }

    @Override
    public boolean isConfigEnabled() {
        return ModuleConfig.getInstance().isModuleEnabled("teleportation");
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        TeleportConfig.getInstance().init();
        TeleportDatabase.getInstance().register(Firefly.getInstance().getDatabase());
        refreshSpawnLocations();
        Bukkit.getPluginManager().registerEvents(new TeleportListener(), Firefly.getInstance());

        SpawnCommand.getCommand().register(Firefly.getInstance());
        SetSpawnCommand.getCommand().register(Firefly.getInstance());
        SetFirstSpawnCommand.getCommand().register(Firefly.getInstance());

        BackCommand.getCommand().register(Firefly.getInstance());
        DBackCommand.getCommand().register(Firefly.getInstance());

        TPACommand.getCommand().register(Firefly.getInstance());
        TPAHereCommand.getCommand().register(Firefly.getInstance());
        TPDenyCommand.getCommand().register(Firefly.getInstance());
        TPAcceptCommand.getCommand().register(Firefly.getInstance());

        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        TeleportConfig.getInstance().reload();
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
            if (sendMessage) {
                TeleportConfig.getInstance().getLocationInvalidMessage().sendMessage(player);
            }
            Loggers.warn(Firefly.getInstance().getComponentLogger(), invalid);
            return false;
        }
        player.teleportAsync(location).thenAccept(success -> {
            if (!sendMessage) {
                return;
            }
            if (success) {
                TeleportConfig.getInstance().getSpawnTeleportedToSpawnMessage().sendMessage(player);
            } else {
                MessageConfig.getInstance().getErrorOccurredMessage().sendMessage(player);
            }
        });
        return true;
    }

    public void sendToSpawn(boolean firstSpawn, @NotNull Player player, @Nullable CommandSender sender, boolean sendMessage) {
        if (sendToSpawn(firstSpawn, player, sendMessage) && sender != null) {
            TeleportConfig.getInstance().getSpawnSentPlayerToSpawnMessage(player).sendMessage(sender);
        }
    }

    // /back

    public void setLastLocation(@NotNull HumanEntity humanEntity, @Nullable Location location) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(humanEntity.getUniqueId());
        if (data == null) {
            return;
        }
        if (location == null) {
            data.removeLastTeleportLocation();
        } else {
            data.setLastTeleportLocation(location);
        }
    }

    public @Nullable Location getLastLocation(@NotNull HumanEntity humanEntity) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(humanEntity.getUniqueId());
        if (data == null) {
            return null;
        }
        return data.getLastTeleportLocation();
    }

}
