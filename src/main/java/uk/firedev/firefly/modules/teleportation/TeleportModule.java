package uk.firedev.firefly.modules.teleportation;

import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
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
import uk.firedev.firefly.utils.TeleportWarmup;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportModule implements Module {

    private static TeleportModule instance;

    private boolean loaded;
    private Location spawnLocation;
    private Location firstSpawnLocation;

    private TeleportModule() {}

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
    public void init() {
        if (isLoaded()) {
            return;
        }
        TeleportConfig.getInstance().init();
        TeleportDatabase.getInstance().register(Firefly.getInstance().getDatabase());
        refreshSpawnLocations();
        Bukkit.getPluginManager().registerEvents(new TeleportListener(), Firefly.getInstance());

        loaded = true;
    }

    @Override
    public void registerCommands(@NotNull Commands registrar) {
        registrar.register(new SpawnCommand().get());
        registrar.register(new SetSpawnCommand().get());
        registrar.register(new SetFirstSpawnCommand().get());

        registrar.register(new BackCommand().get());
        registrar.register(new DBackCommand().get());

        registrar.register(new TPACommand().get());
        registrar.register(new TPAHereCommand().get());
        registrar.register(new TPDenyCommand().get());
        registrar.register(new TPAcceptCommand().get());
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
                TeleportConfig.getInstance().getLocationInvalidMessage().send(player);
            }
            Loggers.warn(Firefly.getInstance().getComponentLogger(), invalid);
            return false;
        }
        int warmup = firstSpawn ? 0 : TeleportConfig.getInstance().getSpawnWarmupSeconds();
        new TeleportWarmup(player, location, warmup)
            .withSuccessMessage(TeleportConfig.getInstance()::getSpawnTeleportedToSpawnMessage)
            .start();
        return true;
    }

    public void sendToSpawn(boolean firstSpawn, @NotNull Player player, @Nullable CommandSender sender, boolean sendMessage) {
        if (sendToSpawn(firstSpawn, player, sendMessage) && sender != null) {
            TeleportConfig.getInstance().getSpawnSentPlayerToSpawnMessage(player).send(sender);
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
