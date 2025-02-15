package uk.firedev.firefly.modules.playtime;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.database.DatabaseModule;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.database.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaytimeDatabase implements DatabaseModule {

    private static PlaytimeDatabase instance;

    private PlaytimeDatabase() {}

    public static PlaytimeDatabase getInstance() {
        if (instance == null) {
            instance = new PlaytimeDatabase();
        }
        return instance;
    }

    @Override
    public void init() {
        try {
            Firefly.getInstance().getDatabase().addColumn("firefly_players", "playtime", "long");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull Map<UUID, Long> getPlaytimes() {
        try (PreparedStatement statement = Firefly.getInstance().getDatabase().getConnection().prepareStatement("SELECT * FROM firefly_players")) {
            ResultSet set = statement.executeQuery();
            Map<UUID, Long> playtimeMap = new HashMap<>();
            while (set.next()) {
                UUID uuid = UUID.fromString(set.getString("uuid"));
                long playtime = set.getLong("playtime");
                playtimeMap.put(uuid, playtime);
            }
            return playtimeMap;
        } catch (SQLException ex) {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to fetch user playtimes from Database.", ex);
            return Map.of();
        }
    }

    public boolean saveToDatabase(@NotNull UUID playerUUID, final long playtime) {
        try (PreparedStatement statement = Firefly.getInstance().getDatabase().getConnection().prepareStatement("UPDATE firefly_players SET playtime = ? WHERE uuid = ?")) {
            statement.setLong(1, playtime);
            statement.setString(2, playerUUID.toString());
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to set playtime for player " + playerUUID, ex);
            return false;
        }
    }

    @Override
    public void save() {
        PlaytimeModule.getInstance().saveAllPlaytimes();
    }

}
