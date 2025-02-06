package uk.firedev.firefly.database;

import org.bukkit.Bukkit;
import org.bukkit.block.data.type.Fire;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.database.SQLiteDatabase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MainConfig;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Database extends SQLiteDatabase {

    private final Map<String, String> columns = new HashMap<>();

    public Database(@NotNull Firefly firefly) {
        super(firefly);
        columns.put("uuid", "VARCHAR(36) NOT NULL PRIMARY KEY");
    }

    @Override
    public void save() {}

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

    public boolean createPlayerEntryIfNeeded(@NotNull UUID playerUUID) {
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
