package uk.firedev.firefly.modules.teleportation.tpa;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;

import java.util.*;

public class TPAHandler {

    private static TPAHandler instance;

    private TPAHandler() {}

    public static TPAHandler getInstance() {
        if (instance == null) {
            instance = new TPAHandler();
        }
        return instance;
    }

    public void sendRequest(@NotNull Player target, @NotNull Player sender, @NotNull TPARequest.TPADirection direction) {
        // Check if the target is the sender
        if (target.equals(sender)) {
            TeleportConfig.getInstance().getTpaCannotRequestSelfMessage().sendMessage(sender);
            return;
        }
        // Create the request and cache it
        new TPARequest(sender, target, direction).send();

        // Create the accept/deny message replacer
        ComponentReplacer acceptDenyReplacer = ComponentReplacer.create(Map.of(
            "accept", TeleportConfig.getInstance().getTpaAcceptClickMessage(sender).getMessage(),
            "deny", TeleportConfig.getInstance().getTpaDenyClickMessage(sender).getMessage()
        ));

        // Send the message
        switch (direction) {
            case SENDER_TO_TARGET -> {
                // Tell the sender they requested the teleport
                TeleportConfig.getInstance().getTpaToRequestSenderMessage(target).sendMessage(sender);
                // Tell the target the request was sent
                TeleportConfig.getInstance().getTpaToRequestTargetMessage(sender).applyReplacer(acceptDenyReplacer).sendMessage(target);
            }
            case TARGET_TO_SENDER -> {
                // Tell the sender they requested the teleport
                TeleportConfig.getInstance().getTpaHereRequestSenderMessage(target).sendMessage(sender);
                // Tell the target the request was sent
                TeleportConfig.getInstance().getTpaHereRequestTargetMessage(sender).applyReplacer(acceptDenyReplacer).sendMessage(target);
            }
        }
    }

    public void acceptRequest(@NotNull Player player) {
        TPARequest request = TPARequest.getRequest(player.getUniqueId());
        if (request == null) {
            return;
        }
        request.accept();
    }

    public void denyRequest(@NotNull Player player) {
        TPARequest request = TPARequest.getRequest(player.getUniqueId());
        if (request == null) {
            return;
        }
        request.deny();
    }

}
