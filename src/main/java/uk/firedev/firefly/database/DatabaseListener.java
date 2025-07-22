package uk.firedev.firefly.database;

import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import uk.firedev.firefly.Firefly;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class DatabaseListener implements Listener {

    @EventHandler
    public void onLogin(AsyncPlayerConnectionConfigureEvent event) {
        UUID uuid = event.getConnection().getProfile().getId();
        if (uuid == null) {
            return; // This should never happen.
        }
        Firefly.getInstance().getDatabase().loadPlayerData(uuid);
    }

}
