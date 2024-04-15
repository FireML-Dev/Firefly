package uk.firedev.skylight.database;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.database.SQLiteDatabase;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.modules.nickname.NicknameManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class Database extends SQLiteDatabase {

    private static Database instance = null;

    private Database() {
        super(Skylight.getInstance());
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    @Override
    public void setup(JavaPlugin plugin) {
        super.setup(plugin);
        initTables();
    }

    public void load() {
        if (!Skylight.getInstance().isEnabled()) {
            return;
        }
    }

    public void unload() {
        closeConnection();
    }

    public void initTables() {

        try (Statement statement = getConnection().createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS skylight_players(uuid varchar primary key)");
        } catch (SQLException e) {
            Loggers.log(Level.SEVERE, Skylight.getInstance().getLogger(), "Failed to initialize the database. Disabling Skylight.", e);
            Bukkit.getPluginManager().disablePlugin(Skylight.getInstance());
        }

        List<String> addColumns = List.of(
                // Nickname
                "ALTER TABLE skylight_players ADD COLUMN nickname varchar"
        );

        addColumns.forEach(s -> {
            try (Statement statement = getConnection().createStatement()) {
                statement.execute(s);
                Loggers.info(Skylight.getInstance().getLogger(), "Created new database column.");
            } catch (SQLException ignored) { }
        });

    }

    public @NotNull CompletableFuture<Boolean> checkPlayerDatabaseEntry(@NotNull UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = getConnection().prepareStatement("INSERT OR IGNORE INTO skylight_players (uuid) VALUES (?)")) {
                statement.setString(1, playerUUID.toString());
                statement.executeUpdate();
                return true;
            } catch (SQLException ex) {
                Loggers.log(Level.SEVERE, Skylight.getInstance().getLogger(), "Failed to add user to Database.", ex);
                return false;
            }
        });
    }

    public @NotNull CompletableFuture<@NotNull Map<UUID, String>> getNicknames() {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM skylight_players")) {
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
                Loggers.log(Level.SEVERE, Skylight.getInstance().getLogger(), "Failed to fetch user nicknames from Database.", ex);
                return Map.of();
            }
        });
    }

    public CompletableFuture<Void> setNickname(@NotNull UUID playerUUID, String nickname) {
        if (nickname == null) {
            nickname = "";
        }
        final String finalNickname = nickname;
        CompletableFuture<Void> combinedFuture = checkPlayerDatabaseEntry(playerUUID).thenCompose(result ->
                CompletableFuture.runAsync(() -> {
                    if (result) {
                        try (PreparedStatement statement = getConnection().prepareStatement("UPDATE skylight_players SET nickname = ? WHERE uuid = ?")) {
                            statement.setString(1, finalNickname);
                            statement.setString(2, playerUUID.toString());
                            statement.executeUpdate();
                        } catch (SQLException ex) {
                            Loggers.log(Level.SEVERE, Skylight.getInstance().getLogger(), "Failed to set username for player " + playerUUID, ex);
                        }
                    } else {
                        Loggers.severe(Skylight.getInstance().getLogger(), "Player is not in the database. Not setting their nickname.");
                    }
                }));
        combinedFuture.thenRun(NicknameManager.getInstance()::populateNicknameMap);
        return combinedFuture;
    }

}
