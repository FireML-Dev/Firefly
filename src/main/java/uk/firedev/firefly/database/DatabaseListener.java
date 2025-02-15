package uk.firedev.firefly.database;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.SchedulingType;
import uk.firedev.firefly.Firefly;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseListener implements Listener {

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        createPlayerEntryIfNeeded(event.getPlayer().getUniqueId());
    }

    public void createPlayerEntryIfNeeded(@NotNull UUID playerUUID) {
        SchedulingType.ASYNC.run(() -> {
            Connection connection = Firefly.getInstance().getDatabase().getConnection();
            try (PreparedStatement statement = connection.prepareStatement("INSERT OR IGNORE INTO firefly_players (uuid) VALUES (?)")) {
                statement.setString(1, playerUUID.toString());
                statement.executeUpdate();
            } catch (SQLException ex) {
                Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to add user to Database.", ex);
            }
        }, Firefly.getInstance());
    }

}
