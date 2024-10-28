package uk.firedev.firefly.modules.teleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            TeleportModule.getInstance().sendToSpawn(true, player, false);
        } else if (TeleportConfig.getInstance().isSpawnOnJoin()) {
            TeleportModule.getInstance().sendToSpawn(false, player, false);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (TeleportConfig.getInstance().shouldBackSaveDeath()) {
            Player player = event.getPlayer();
            TeleportModule.getInstance().setLastLocation(player, player.getLocation());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        TeleportModule.getInstance().setLastLocation(player, event.getFrom());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Location customSpawnLocation = TeleportModule.getInstance().getSpawnLocation();
        if (event.getPlayer().getRespawnLocation() == null && customSpawnLocation != null) {
            event.setRespawnLocation(customSpawnLocation);
        }
    }

}
