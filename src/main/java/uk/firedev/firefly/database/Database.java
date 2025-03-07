package uk.firedev.firefly.database;

import com.google.common.cache.Cache;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.database.DatabaseModule;
import uk.firedev.daisylib.api.database.SQLiteDatabase;
import uk.firedev.daisylib.api.database.exceptions.DatabaseLoadException;
import uk.firedev.daisylib.api.utils.PlayerHelper;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MainConfig;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Database extends SQLiteDatabase {

    private final Map<String, String> columns = new HashMap<>();
    private final Map<UUID, PlayerData> playerDataCache = new HashMap<>();

    private BukkitTask unloadCacheTask;

    public Database(@NotNull Firefly firefly) {
        super(firefly);
        columns.put("uuid", "VARCHAR(36) NOT NULL PRIMARY KEY");
    }

    @Override
    public void load() throws DatabaseLoadException {
        super.load();
        Bukkit.getPluginManager().registerEvents(new DatabaseListener(), getPlugin());
    }

    @Override
    public void save() {
        List<UUID> unload = new ArrayList<>();
        playerDataCache.forEach((uuid, playerData) -> {
            playerData.save();
            if (playerData.canUnload()) {
                unload.add(uuid);
            }
        });
        unload.forEach(this::unloadPlayerData);
    }

    @NotNull
    @Override
    public String getTable() {
        return "firefly_players";
    }

    @Override
    public @NotNull Map<String, String> getColumns() {
        return columns;
    }

    @Override
    public long getAutoSaveSeconds() {
        return MainConfig.getInstance().getDatabaseSaveInterval();
    }

    public void loadPlayerData(@NotNull UUID uuid) {
        createPlayerDataIfMissing(uuid);
        try (PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM firefly_players WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet set = ps.executeQuery();
            set.next();
            PlayerData data = new PlayerData(uuid);
            for (DatabaseModule module : getLoadedModules()) {
                if (module instanceof FireflyDatabaseModule fireflyModule) {
                    fireflyModule.load(data, set);
                }
            }
            playerDataCache.put(uuid, data);
            Loggers.info(Firefly.getInstance().getComponentLogger(), "Loaded PlayerData for " + uuid);
        } catch (SQLException exception) {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to load player data for " + uuid, exception);
        }
    }

    public void unloadPlayerData(@NotNull UUID uuid) {
        PlayerData cachedData = playerDataCache.remove(uuid);
        if (cachedData == null) {
            return;
        }
        cachedData.save();
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Unloaded PlayerData for " + uuid);
    }

    public @Nullable PlayerData getPlayerData(@NotNull UUID uuid) {
        // Player has never joined the server
        if (PlayerHelper.getOfflinePlayer(uuid) == null) {
            return null;
        }
        PlayerData data = playerDataCache.get(uuid);
        if (data == null) {
            loadPlayerData(uuid);
            data = playerDataCache.get(uuid);
        }
        return data;
    }

    public @NotNull PlayerData getPlayerDataOrThrow(@NotNull UUID uuid) {
        return getPlayerDataOrThrow(uuid, "Could not find player data for " + uuid);
    }

    public @NotNull PlayerData getPlayerDataOrThrow(@NotNull UUID uuid, @NotNull String throwMessage) {
        PlayerData data = getPlayerData(uuid);
        if (data == null) {
            throw new RuntimeException(throwMessage);
        }
        return data;
    }

    private void createPlayerDataIfMissing(@NotNull UUID uuid) {
        try (PreparedStatement ps = getConnection().prepareStatement("INSERT OR IGNORE INTO firefly_players (uuid) VALUES (?)")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException exception) {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to create player data for " + uuid, exception);
        }
    }

}
