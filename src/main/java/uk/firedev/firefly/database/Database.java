package uk.firedev.firefly.database;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.database.DatabaseModule;
import uk.firedev.daisylib.api.database.SQLiteDatabase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MainConfig;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class Database extends SQLiteDatabase {

    private static Database instance = null;
    private BukkitTask autoSaveTask = null;
    private boolean loaded;

    private Database() {
        super(Firefly.getInstance());
        initTables();
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void load() {
        if (!Firefly.getInstance().isEnabled() || loaded) {
            return;
        }
        loaded = true;
        startAutoSaveTask();
    }

    public void reload() {
        stopAutoSaveTask();
        startAutoSaveTask();
    }

    @Override
    public void startAutoSaveTask() {
        if (autoSaveTask == null) {
            // seconds * 20 = ticks
            long saveInterval = MainConfig.getInstance().getDatabaseSaveInterval() * 20;
            autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    Firefly.getInstance(),
                    () -> getLoadedModules().forEach(DatabaseModule::save),
                    saveInterval,
                    saveInterval
            );
        }
    }

    @Override
    public void stopAutoSaveTask() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
            autoSaveTask = null;
        }
    }

    public void unload() {
        if (!loaded) {
            return;
        }
        loaded = false;
        closeConnection();
        getLoadedModules().forEach(DatabaseModule::save);
        stopAutoSaveTask();
    }

    public void initTables() {
        try (Statement statement = getConnection().createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS firefly_players(uuid varchar primary key)");
        } catch (SQLException e) {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to initialize the database. Disabling Firefly.", e);
            Bukkit.getPluginManager().disablePlugin(Firefly.getInstance());
        }
    }

    public boolean checkPlayerDatabaseEntry(@NotNull UUID playerUUID) {
        try (PreparedStatement statement = getConnection().prepareStatement("INSERT OR IGNORE INTO firefly_players (uuid) VALUES (?)")) {
            statement.setString(1, playerUUID.toString());
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to add user to Database.", ex);
            return false;
        }
    }

}
