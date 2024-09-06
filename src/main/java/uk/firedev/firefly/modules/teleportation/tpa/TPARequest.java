package uk.firedev.firefly.modules.teleportation.tpa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;

import java.util.UUID;

public class TPARequest {

    private String senderName;
    private UUID senderUuid;
    private UUID targetUuid;
    private TPADirection direction;

    public TPARequest(@NotNull Player sender, @NotNull Player target, @NotNull TPADirection direction) {
        this.senderName = sender.getName();
        this.senderUuid = sender.getUniqueId();
        this.targetUuid = target.getUniqueId();
        this.direction = direction;
    }

    public void accept() {
        Player sender = Bukkit.getPlayer(this.senderUuid);
        Player target = Bukkit.getPlayer(this.targetUuid);
        // If accepter is offline, do not do anything
        if (target == null) {
            return;
        }
        if (sender == null) {
            MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(target);
            return;
        }
        switch (this.direction) {
            case SENDER_TO_TARGET -> attemptTeleportation(sender, target);
            case TARGET_TO_SENDER -> attemptTeleportation(target, sender);
        }

    }

    private void attemptTeleportation(@NotNull Player teleportingPlayer, @NotNull Player teleportTarget) {
        if (teleportTarget.isFlying() || teleportTarget.isGliding()) {
            TeleportConfig.getInstance().getTpaTargetFlyingMessage().sendMessage(teleportingPlayer);
            return;
        }
        TeleportConfig.getInstance().getTpaRequestAcceptedTargetMessage().sendMessage(teleportTarget);
        TeleportConfig.getInstance().getTpaRequestAcceptedTeleporterMessage().sendMessage(teleportingPlayer);
        teleportingPlayer.teleportAsync(teleportTarget.getLocation());
    }

    public void deny() {
        Player sender = Bukkit.getPlayer(this.senderUuid);
        Player target = Bukkit.getPlayer(this.targetUuid);
        // If denier is offline, do not do anything
        if (target == null) {
            return;
        }
        TeleportConfig.getInstance().getTpaRequestDeniedTargetMessage().sendMessage(target);
        if (sender != null) {
            TeleportConfig.getInstance().getTpaRequestDeniedSenderMessage(target).sendMessage(sender);
        }
    }

    public enum TPADirection {
        TARGET_TO_SENDER,
        SENDER_TO_TARGET
    }

    public String getSenderName() {
        return this.senderName;
    }

}
