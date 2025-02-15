package uk.firedev.firefly.modules.nickname;

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

public class NicknameDatabase implements DatabaseModule {

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

    public @NotNull Map<UUID, String> getNicknames() {
        try (PreparedStatement statement = Firefly.getInstance().getDatabase().getConnection().prepareStatement("SELECT * FROM firefly_players")) {
            ResultSet set = statement.executeQuery();
            Map<UUID, String> nicknameMap = new HashMap<>();
            while (set.next()) {
                UUID uuid = UUID.fromString(set.getString("uuid"));
                String nickname = set.getString("nickname");
                if (nickname == null) {
                    nickname = "";
                }
                nicknameMap.put(uuid, nickname);
            }
            return nicknameMap;
        } catch (SQLException ex) {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to fetch user nicknames from Database.", ex);
            return Map.of();
        }
    }

    public boolean saveToDatabase(@NotNull UUID playerUUID, String nickname) {
        if (nickname == null) {
            nickname = "";
        }
        final String finalNickname = nickname;

        try (PreparedStatement statement = Firefly.getInstance().getDatabase().getConnection().prepareStatement("UPDATE firefly_players SET nickname = ? WHERE uuid = ?")) {
            statement.setString(1, finalNickname);
            statement.setString(2, playerUUID.toString());
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to set username for player " + playerUUID, ex);
            return false;
        }
    }

    @Override
    public void save() {
        NicknameModule.getInstance().saveAllNicknames();
    }

}
