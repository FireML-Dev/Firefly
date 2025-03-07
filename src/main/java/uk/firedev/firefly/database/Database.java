package uk.firedev.firefly.database;

import org.bukkit.Bukkit;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Database extends SQLiteDatabase {

    private final Map<String, String> columns = new HashMap<>();
    private final Map<UUID, PlayerData> playerDataCache = new HashMap<>();

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
        playerDataCache.forEach((uuid, playerData) -> playerData.save());
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
            PlayerData data = new PlayerData(uuid);
            for (DatabaseModule module : getLoadedModules()) {
                if (module instanceof FireflyDatabaseModule fireflyModule) {
                    fireflyModule.load(data, set);
                }
            }
            playerDataCache.put(uuid, data);
            System.out.println("Loaded PlayerData for " + uuid);
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
        System.out.println("Unloaded PlayerData for " + uuid);
    }

    public @Nullable PlayerData getPlayerData(@NotNull UUID uuid) {
        // Player has never joined the server
        if (PlayerHelper.getOfflinePlayer(uuid) == null) {
            return null;
        }
        return playerDataCache.get(uuid);
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
