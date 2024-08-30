package uk.firedev.firefly.modules.teleportation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        TeleportManager.getInstance().setLastLocation(player, player.getLocation());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        TeleportManager.getInstance().setLastLocation(player, event.getFrom());
    }

}
