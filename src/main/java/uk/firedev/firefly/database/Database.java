package uk.firedev.firefly.database;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.database.SQLiteDatabase;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MainConfig;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database extends SQLiteDatabase {

    private static Database instance = null;
    private List<DatabaseModule> registeredModules;
    private MyScheduledTask autoSaveTask = null;
    private boolean loaded;

    private Database() {
        super(Firefly.getInstance());
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    @Override
    public void setup(JavaPlugin plugin) {
        super.setup(plugin);
        initTables();
    }

    public void load() {
        if (!Firefly.getInstance().isEnabled() || loaded) {
            return;
        }
        registeredModules = new ArrayList<>();
        if (autoSaveTask == null) {
            // seconds * 20 = ticks
            long saveInterval = MainConfig.getInstance().getDatabaseSaveInterval() * 20;
            autoSaveTask = Firefly.getScheduler().runTaskTimerAsynchronously(
                    () -> registeredModules.forEach(DatabaseModule::save),
                    saveInterval,
                    saveInterval
            );
        }
    }

    public void unload() {
        if (!loaded) {
            return;
        }
        closeConnection();
        registeredModules.forEach(DatabaseModule::save);
        registeredModules = null;
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
            autoSaveTask = null;
        }
    }

    public void initTables() {
        try (Statement statement = getConnection().createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS firefly_players(uuid varchar primary key)");
        } catch (SQLException e) {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to initialize the database. Disabling Firefly.", e);
            Bukkit.getPluginManager().disablePlugin(Firefly.getInstance());
        }
    }

    public void registerModule(@NotNull DatabaseModule module) {
        if (registeredModules.contains(module)) {
            return;
        }
        module.init();
        registeredModules.add(module);
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
