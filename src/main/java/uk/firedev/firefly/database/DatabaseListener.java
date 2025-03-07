package uk.firedev.firefly.database;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.firedev.firefly.Firefly;

public class DatabaseListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Cache this player's data
        Firefly.getInstance().getDatabase().loadPlayerData(event.getPlayer().getUniqueId());
    }

}
