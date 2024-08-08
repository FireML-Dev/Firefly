package uk.firedev.firefly.database;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.database.SQLiteDatabase;
import uk.firedev.firefly.Firefly;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class Database extends SQLiteDatabase {

    private static Database instance = null;

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
        if (!Firefly.getInstance().isEnabled()) {
            return;
        }
    }

    public void unload() {
        closeConnection();
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
