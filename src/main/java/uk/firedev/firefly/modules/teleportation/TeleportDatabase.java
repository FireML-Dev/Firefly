package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.utils.DatabaseUtils;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.database.FireflyDatabaseModule;
import uk.firedev.firefly.database.PlayerData;

import java.sql.*;

public class TeleportDatabase implements FireflyDatabaseModule {

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

    @Override
    public void save() {}

    @Override
    public void load(@NotNull PlayerData data, @NotNull ResultSet set) throws SQLException {
        String locationStr = set.getString("lastTeleportLocation");
        if (locationStr == null) {
            return;
        }
        Location location = DatabaseUtils.parseLocation(locationStr);
        if (location == null) {
            return;
        }
        data.setLastTeleportLocation(location);
    }

    @Override
    public void save(@NotNull PlayerData data, @NotNull Connection connection) throws SQLException {
        Location location = data.getLastTeleportLocation();
        if (location == null) {
            return;
        }
        try (PreparedStatement ps = connection.prepareStatement("UPDATE firefly_players SET lastTeleportLocation = ? WHERE uuid = ?")) {
            ps.setString(1, DatabaseUtils.prepareLocation(location));
            ps.setString(2, data.getUuid().toString());
            ps.executeUpdate();
        }
    }
}
