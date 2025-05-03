package uk.firedev.firefly.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO allow configurable messages
public class TeleportWarmup {

    private static final List<UUID> teleportingPlayers = new ArrayList<>();

    public static void teleportPlayer(@NotNull Player player, @NotNull Location location, int duration) {
        new TeleportRunnable(player, location, duration).start();
    }

    private static class TeleportRunnable extends BukkitRunnable {

        private boolean complete = false;
        private int duration;

        private final @NotNull Location location;
        private final @NotNull Player player;
        private final @NotNull Location initialLocation;

        public TeleportRunnable(@NotNull Player player, @NotNull Location location, int duration) {

            this.location = location;
            this.duration = duration;
            this.player = player;

            Location initial = player.getLocation();
            initial.setYaw(0);
            initial.setPitch(0);
            this.initialLocation = initial;

            if (this.duration <= 0 || player.hasPermission("firefly.teleport.warmupbypass")) {
                successfulTeleport();
            }
        }

        public void start() {
            if (complete) {
                return;
            }
            if (teleportingPlayers.contains(player.getUniqueId())) {
                sendMessage(
                    MessageConfig.getInstance().getTeleportWarmupAlreadyTeleportingMessage()
                );
                return;
            }
            teleportingPlayers.add(player.getUniqueId());
            runTaskTimer(Firefly.getInstance(), 0, 20);
        }

        private void cancelTeleport() {
            sendMessage(
                MessageConfig.getInstance().getTeleportWarmupCancelledMessage()
            );
            complete = true;
            teleportingPlayers.remove(player.getUniqueId());
        }

        private void successfulTeleport() {
            player.teleportAsync(location).thenAccept(success -> {
                if (success) {
                    sendMessage(
                        MessageConfig.getInstance().getTeleportWarmupCompleteMessage()
                    );
                } else {
                    sendMessage(
                        MessageConfig.getInstance().getErrorOccurredMessage()
                    );
                }
            });
            complete = true;
            teleportingPlayers.remove(player.getUniqueId());
        }

        @Override
        public void run() {
            if (hasMoved()) {
                cancelTeleport();
                this.cancel();
                return;
            }

            if (duration > 0) {
                sendWaitingMessage();
                duration--;
                return;
            }

            successfulTeleport();
            this.cancel();
        }

        private void sendWaitingMessage() {
            ComponentMessage message = MessageConfig.getInstance().getTeleportWarmupMessage();
            message.replace("time", String.valueOf(duration));
            sendMessage(message);
        }

        private boolean hasMoved() {
            Location playerLocation = player.getLocation();
            playerLocation.setYaw(0);
            playerLocation.setPitch(0);
            return !initialLocation.equals(playerLocation);
        }

        private void sendMessage(@NotNull ComponentMessage message) {
            message.sendMessage(player);
        }

    }

}
