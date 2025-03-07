package uk.firedev.firefly.modules.nickname;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.database.DatabaseModule;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.database.FireflyDatabaseModule;
import uk.firedev.firefly.database.PlayerData;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NicknameDatabase implements FireflyDatabaseModule {

    private static NicknameDatabase instance;

    private NicknameDatabase() {}

    public static NicknameDatabase getInstance() {
        if (instance == null) {
            instance = new NicknameDatabase();
        }
        return instance;
    }

    @Override
    public void init() {
        try {
            Firefly.getInstance().getDatabase().addColumn("firefly_players", "nickname", "varchar");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save() {}

    @Override
    public void load(@NotNull PlayerData data, @NotNull ResultSet set) throws SQLException {
        String nickname = set.getString("nickname");
        if (nickname != null) {
            data.setRawNickname(nickname);
        }
    }

    @Override
    public void save(@NotNull PlayerData data, @NotNull Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE firefly_players SET nickname = ? WHERE uuid = ?")) {
            ps.setString(1, data.getRawNickname());
            ps.setString(2, data.getUuid().toString());
            ps.executeUpdate();
        }
    }

}
