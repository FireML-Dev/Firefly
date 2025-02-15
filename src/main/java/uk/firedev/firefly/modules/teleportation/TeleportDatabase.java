package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.database.DatabaseModule;
import uk.firedev.daisylib.api.utils.DatabaseUtils;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.database.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportDatabase implements DatabaseModule {

    private static TeleportDatabase instance;

    private TeleportDatabase() {}

    public static TeleportDatabase getInstance() {
        if (instance == null) {
            instance = new TeleportDatabase();
        }
        return instance;
    }

    @Override
    public void init() {
        try {
            Firefly.getInstance().getDatabase().addColumn("firefly_players", "lastTeleportLocation", "VARCHAR(255)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Last Location for /back

    public boolean saveLastLocationToDatabase(@NotNull UUID playerUUID, @NotNull Location location) {
        try (PreparedStatement statement = Firefly.getInstance().getDatabase().getConnection().prepareStatement("UPDATE firefly_players SET lastTeleportLocation = ? WHERE uuid = ?")) {
            statement.setString(1, DatabaseUtils.prepareLocation(location));
            statement.setString(2, playerUUID.toString());
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to set last teleport location for player " + playerUUID, ex);
            return false;
        }
    }

    public @NotNull Map<UUID, Location> getLastLocations() {
        try (PreparedStatement statement = Firefly.getInstance().getDatabase().getConnection().prepareStatement("SELECT * FROM firefly_players")) {
            ResultSet set = statement.executeQuery();
            Map<UUID, Location> lastLocationMap = new HashMap<>();
            while (set.next()) {
                String uuidString = set.getString("uuid");
                String locationString = set.getString("lastTeleportLocation");
                if (locationString == null || uuidString == null) {
                    continue;
                }
                UUID uuid = UUID.fromString(set.getString("uuid"));
                Location location = DatabaseUtils.parseLocation(locationString);
                if (location == null) {
                    continue;
                }
                lastLocationMap.put(uuid, location);
            }
            return lastLocationMap;
        } catch (SQLException ex) {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to fetch user last teleport locations from Database.", ex);
            return Map.of();
        }
    }

    @Override
    public void save() {
        TeleportModule.getInstance().saveAllLastLocations();
    }

}
