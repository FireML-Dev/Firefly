package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.database.DatabaseModule;
import uk.firedev.daisylib.api.utils.LocationHelper;
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
        try (Statement statement = Database.getInstance().getConnection().createStatement()) {
            statement.execute("ALTER TABLE firefly_players ADD COLUMN lastTeleportLocation VARCHAR(255)");
            Loggers.info(Firefly.getInstance().getComponentLogger(), "Created last location database column.");
        } catch (SQLException ignored) {}
    }

    // Last Location for /back

    public boolean saveLastLocationToDatabase(@NotNull UUID playerUUID, @NotNull Location location) {
        if (Database.getInstance().checkPlayerDatabaseEntry(playerUUID)) {
            try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement("UPDATE firefly_players SET lastTeleportLocation = ? WHERE uuid = ?")) {
                statement.setString(1, LocationHelper.convertToString(location, true));
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
                return true;
            } catch (SQLException ex) {
                Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to set last teleport location for player " + playerUUID, ex);
                return false;
            }
        } else {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Player is not in the database. Not setting their last teleport location.");
            return false;
        }
    }

    public @NotNull Map<UUID, Location> getLastLocations() {
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement("SELECT * FROM firefly_players")) {
            ResultSet set = statement.executeQuery();
            Map<UUID, Location> lastLocationMap = new HashMap<>();
            while (set.next()) {
                String uuidString = set.getString("uuid");
                String locationString = set.getString("lastTeleportLocation");
                if (locationString == null || uuidString == null) {
                    continue;
                }
                UUID uuid = UUID.fromString(set.getString("uuid"));
                Location location = LocationHelper.getFromString(locationString);
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
