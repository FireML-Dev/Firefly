package uk.firedev.skylight.modules.nickname;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.database.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NicknameDatabase {

    private static NicknameDatabase instance;

    private NicknameDatabase() {}

    public static NicknameDatabase getInstance() {
        if (instance == null) {
            instance = new NicknameDatabase();
        }
        return instance;
    }

    public @NotNull CompletableFuture<@NotNull Map<UUID, String>> getNicknames() {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement("SELECT * FROM skylight_players")) {
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
                Loggers.error(Skylight.getInstance().getComponentLogger(), "Failed to fetch user nicknames from Database.", ex);
                return Map.of();
            }
        });
    }

    public CompletableFuture<Void> setNickname(@NotNull UUID playerUUID, String nickname) {
        if (nickname == null) {
            nickname = "";
        }
        final String finalNickname = nickname;
        CompletableFuture<Void> combinedFuture = Database.getInstance().checkPlayerDatabaseEntry(playerUUID).thenCompose(result ->
                CompletableFuture.runAsync(() -> {
                    if (result) {
                        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement("UPDATE skylight_players SET nickname = ? WHERE uuid = ?")) {
                            statement.setString(1, finalNickname);
                            statement.setString(2, playerUUID.toString());
                            statement.executeUpdate();
                        } catch (SQLException ex) {
                            Loggers.error(Skylight.getInstance().getComponentLogger(), "Failed to set username for player " + playerUUID, ex);
                        }
                    } else {
                        Loggers.error(Skylight.getInstance().getComponentLogger(), "Player is not in the database. Not setting their nickname.");
                    }
                }));
        combinedFuture.thenRun(NicknameManager.getInstance()::populateNicknameMap);
        return combinedFuture;
    }

}
