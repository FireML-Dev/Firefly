package uk.firedev.firefly.modules.teleportation;

import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.util.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
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

public class TeleportModule implements Module {

    private static TeleportModule instance;

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
        TeleportDatabase.getInstance().register(Firefly.getInstance().getDatabase());
        refreshSpawnLocations();
        Bukkit.getPluginManager().registerEvents(new TeleportListener(), Firefly.getInstance());
        registerCommands();
    }

    private void registerCommands() {
        new SpawnCommand().initCommand();
        new SetSpawnCommand().initCommand();
        new SetFirstSpawnCommand().initCommand();

        new BackCommand().initCommand();
        new DBackCommand().initCommand();

        new TPACommand().initCommand();
        new TPAHereCommand().initCommand();
        new TPDenyCommand().initCommand();
        new TPAcceptCommand().initCommand();
    }

    @Override
    public void reload() {
        TeleportConfig.getInstance().reload();
    }

    @Override
    public void unload() {}

    // /spawn

    public void refreshSpawnLocations() {
        if (!isConfigEnabled()) {
            spawnLocation = null;
            firstSpawnLocation = null;
            return;
        }
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
        if (!isConfigEnabled()) {
            return false;
        }
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
        if (!isConfigEnabled()) {
            return;
        }
        if (sendToSpawn(firstSpawn, player, sendMessage) && sender != null) {
            TeleportConfig.getInstance().getSpawnSentPlayerToSpawnMessage(player).send(sender);
        }
    }

    // /back

    public void setLastLocation(@NotNull HumanEntity humanEntity, @Nullable Location location) {
        if (!isConfigEnabled()) {
            return;
        }
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
        if (!isConfigEnabled()) {
            return null;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(humanEntity.getUniqueId());
        if (data == null) {
            return null;
        }
        return data.getLastTeleportLocation();
    }

}
