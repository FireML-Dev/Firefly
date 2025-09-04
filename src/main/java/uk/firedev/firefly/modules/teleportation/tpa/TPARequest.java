package uk.firedev.firefly.modules.teleportation.tpa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;
import uk.firedev.firefly.utils.TeleportWarmup;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TPARequest {

    private static final Map<UUID, TPARequest> activeRequests = new HashMap<>();

    private final UUID sender;
    private final UUID target;
    private final TPADirection direction;

    public TPARequest(@NotNull Player sender, @NotNull Player target, @NotNull TPADirection direction) {
        this.sender = sender.getUniqueId();
        this.target = target.getUniqueId();
        this.direction = direction;
    }

    public void send() {
        activeRequests.put(target, this);
        Bukkit.getScheduler().runTaskLater(Firefly.getInstance(), () -> {
            TPARequest request = activeRequests.get(target);
            if (request == this) {
                activeRequests.remove(target);
            }
        }, TeleportConfig.getInstance().getTpaRequestExpiry() * 20L);
    }

    public void accept() {
        activeRequests.remove(target);
        Player sender = Bukkit.getPlayer(this.sender);
        Player target = Bukkit.getPlayer(this.target);
        // If target is offline, do not do anything
        if (target == null) {
            return;
        }
        // If sender is offline, send player not found message
        if (sender == null) {
            MessageConfig.getInstance().getPlayerNotFoundMessage().send(target);
            return;
        }
        // Attempt the teleport
        switch (this.direction) {
            case SENDER_TO_TARGET -> attemptTeleportation(sender, target);
            case TARGET_TO_SENDER -> attemptTeleportation(target, sender);
        }
    }

    private void attemptTeleportation(@NotNull Player teleportingPlayer, @NotNull Player teleportTarget) {
        if (teleportTarget.isFlying() || teleportTarget.isGliding()) {
            TeleportConfig.getInstance().getTpaTargetFlyingMessage().send(teleportingPlayer);
            return;
        }
        TeleportConfig.getInstance().getTpaRequestAcceptedTargetMessage().send(teleportTarget);
        TeleportConfig.getInstance().getTpaRequestAcceptedTeleporterMessage().send(teleportingPlayer);
        TeleportWarmup.teleportPlayer(
            teleportingPlayer,
            teleportTarget.getLocation(),
            TeleportConfig.getInstance().getTPAWarmupSeconds()
        );
    }

    public void deny() {
        activeRequests.remove(target);
        Player sender = Bukkit.getPlayer(this.sender);
        Player target = Bukkit.getPlayer(this.target);
        // If target is offline, do not do anything
        if (target == null) {
            return;
        }
        // Tell the target they denied the request
        TeleportConfig.getInstance().getTpaRequestDeniedTargetMessage().send(target);
        // Tell the sender their request was denied if they are online
        if (sender != null) {
            TeleportConfig.getInstance().getTpaRequestDeniedSenderMessage(target).send(sender);
        }
    }

    public enum TPADirection {
        TARGET_TO_SENDER,
        SENDER_TO_TARGET
    }

    public static @Nullable TPARequest getRequest(@NotNull UUID target) {
        return activeRequests.get(target);
    }

}
