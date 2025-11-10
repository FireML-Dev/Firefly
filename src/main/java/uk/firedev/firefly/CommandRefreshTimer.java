package uk.firedev.firefly;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class CommandRefreshTimer {

    private BukkitTask task;

    public void start() {
        if (this.task != null) {
            return;
        }
        this.task = Bukkit.getScheduler().runTaskTimer(
            Firefly.getInstance(),
            () -> Bukkit.getOnlinePlayers().forEach(Player::updateCommands),
            20L,
            20L
        );
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    private void runUpdate() {
        Bukkit.getScheduler().runTaskLater(
            Firefly.getInstance(),
            () -> Bukkit.getOnlinePlayers().forEach(Player::updateCommands),
            20L
        );
    }

}
