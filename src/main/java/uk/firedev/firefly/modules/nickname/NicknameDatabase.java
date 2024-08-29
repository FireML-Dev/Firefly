package uk.firedev.firefly.modules.nickname;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.database.DatabaseModule;

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
        try (Statement statement = Database.getInstance().getConnection().createStatement()) {
            statement.execute("ALTER TABLE firefly_players ADD COLUMN nickname varchar");
            Loggers.info(Firefly.getInstance().getComponentLogger(), "Created nickname database column.");
        } catch (SQLException ignored) {}
    }

    public @NotNull Map<UUID, String> getNicknames() {
        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement("SELECT * FROM firefly_players")) {
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

        if (Database.getInstance().checkPlayerDatabaseEntry(playerUUID)) {
            try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement("UPDATE firefly_players SET nickname = ? WHERE uuid = ?")) {
                statement.setString(1, finalNickname);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
                return true;
            } catch (SQLException ex) {
                Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to set username for player " + playerUUID, ex);
                return false;
            }
        } else {
            Loggers.error(Firefly.getInstance().getComponentLogger(), "Player is not in the database. Not setting their nickname.");
            return false;
        }
    }

    @Override
    public void save() {
        NicknameManager.getInstance().saveAllNicknames();
    }

}
