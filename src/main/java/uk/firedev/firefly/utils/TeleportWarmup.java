package uk.firedev.firefly.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.messagelib.message.ComponentMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class TeleportWarmup {

    private static final List<UUID> teleportingPlayers = new ArrayList<>();

    private final Player player;
    private final Location location;
    private final int duration;

    private Supplier<ComponentMessage> waitingMessage;
    private Supplier<ComponentMessage> failedMessage;
    private Supplier<ComponentMessage> successMessage;

    public TeleportWarmup(@NotNull Player player, @NotNull Location location, int duration) {
        this.player = player;
        this.location = location;
        this.duration = duration;
    }

    public TeleportWarmup withWaitingMessage(@NotNull Supplier<ComponentMessage> message) {
        this.waitingMessage = message;
        return this;
    }

    public TeleportWarmup withFailedMessage(@NotNull Supplier<ComponentMessage> message) {
        this.failedMessage = message;
        return this;
    }

    public TeleportWarmup withSuccessMessage(@NotNull Supplier<ComponentMessage> message) {
        this.successMessage = message;
        return this;
    }

    public void start() {
        new TeleportRunnable(this).start();
    }

    private static class TeleportRunnable extends BukkitRunnable {

        private final TeleportWarmup warmup;
        private boolean complete = false;
        private int remainingTime;

        private final @NotNull Location initialLocation;

        public TeleportRunnable(@NotNull TeleportWarmup warmup) {
            this.warmup = warmup;
            this.remainingTime = warmup.duration;
            Location initial = warmup.player.getLocation();
            initial.setYaw(0);
            initial.setPitch(0);
            this.initialLocation = initial;

            if (warmup.duration <= 0 || warmup.player.hasPermission("firefly.teleport.warmupbypass")) {
                successfulTeleport();
            }
        }

        public void start() {
            if (complete) {
                return;
            }
            if (teleportingPlayers.contains(warmup.player.getUniqueId())) {
                sendMessage(
                    MessageConfig.getInstance().getTeleportWarmupAlreadyTeleportingMessage()
                );
                return;
            }
            teleportingPlayers.add(warmup.player.getUniqueId());
            runTaskTimer(Firefly.getInstance(), 0, 20);
        }

        private void cancelTeleport() {
            if (warmup.failedMessage == null) {
                sendMessage(MessageConfig.getInstance().getTeleportWarmupCancelledMessage());
            } else {
                sendMessage(warmup.failedMessage.get());
            }
            complete = true;
            teleportingPlayers.remove(warmup.player.getUniqueId());
        }

        private void successfulTeleport() {
            warmup.player.teleportAsync(warmup.location).thenAccept(success -> {
                if (success) {
                    if (warmup.successMessage == null) {
                        sendMessage(MessageConfig.getInstance().getTeleportWarmupCompleteMessage());
                    } else {
                        sendMessage(warmup.successMessage.get());
                    }
                } else {
                    sendMessage(
                        MessageConfig.getInstance().getErrorOccurredMessage()
                    );
                }
            });
            complete = true;
            teleportingPlayers.remove(warmup.player.getUniqueId());
        }

        @Override
        public void run() {
            if (hasMoved()) {
                cancelTeleport();
                this.cancel();
                return;
            }

            if (remainingTime > 0) {
                sendWaitingMessage();
                remainingTime--;
                return;
            }

            successfulTeleport();
            this.cancel();
        }

        private void sendWaitingMessage() {
            if (warmup.waitingMessage == null) {
                sendMessage(MessageConfig.getInstance().getTeleportWarmupMessage().replace("{time}", remainingTime));
            } else {
                sendMessage(warmup.waitingMessage.get().replace("{time}", remainingTime));
            }
        }

        private boolean hasMoved() {
            Location playerLocation = warmup.player.getLocation();
            playerLocation.setYaw(0);
            playerLocation.setPitch(0);
            return !initialLocation.equals(playerLocation);
        }

        private void sendMessage(@NotNull ComponentMessage message) {
            message.send(warmup.player);
        }

    }

}
