package uk.firedev.firefly.modules.playtime;

import org.jetbrains.annotations.NotNull;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.database.FireflyDatabaseModule;
import uk.firedev.firefly.database.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaytimeDatabase implements FireflyDatabaseModule {

    private static PlaytimeDatabase instance;

    private final Database database;

    private PlaytimeDatabase() {
        this.database = Firefly.getInstance().getDatabase();
    }

    public static PlaytimeDatabase getInstance() {
        if (instance == null) {
            instance = new PlaytimeDatabase();
        }
        return instance;
    }

    @Override
    public void init() {
        try {
            database.addColumn("firefly_players", "playtime", "long");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save() {}

    @Override
    public void load(@NotNull PlayerData data, @NotNull ResultSet set) throws SQLException {
        data.setPlaytime(set.getLong("playtime"));
    }

    @Override
    public void save(@NotNull PlayerData data, @NotNull Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE firefly_players SET playtime = ? WHERE uuid = ?")) {
            ps.setLong(1, data.getPlaytime());
            ps.setString(2, data.getUuid().toString());
            ps.executeUpdate();
        }
    }

}
