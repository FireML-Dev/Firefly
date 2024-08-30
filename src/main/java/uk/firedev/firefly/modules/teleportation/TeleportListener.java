package uk.firedev.firefly.modules.teleportation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TeleportListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            TeleportManager.getInstance().sendToSpawn(true, player);
        } else if (TeleportConfig.getInstance().isSpawnOnJoin()) {
            TeleportManager.getInstance().sendToSpawn(false, player);
        }
    }

}
