package uk.firedev.firefly.modules.teleportation.tpa;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.ArgumentSuggestions;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TPAHandler {

    private static TPAHandler instance;

    private Map<UUID, List<TPARequest>> tpaCache;

    private TPAHandler() {
        tpaCache = new HashMap<>();
    }

    public static TPAHandler getInstance() {
        if (instance == null) {
            instance = new TPAHandler();
        }
        return instance;
    }

    public Map<UUID, List<TPARequest>> getTpaCache() {
        return Map.copyOf(tpaCache);
    }

    private Map<UUID, List<TPARequest>> getEditableTpaCache() {
        return tpaCache;
    }

    private List<TPARequest> getTpaRequests(@NotNull Player player) {
        return getEditableTpaCache().computeIfAbsent(player.getUniqueId(), handling -> new ArrayList<>());
    }

    public List<String> getTpaRequestNames(@NotNull Player player) {
        return getTpaRequests(player).stream().map(TPARequest::getSenderName).toList();
    }

    public Argument<String> getTpaRequestsArgument() {
        return new StringArgument("request").includeSuggestions(ArgumentSuggestions.stringsAsync(info -> {
            if (!(info.sender() instanceof Player player)) {
                return CompletableFuture.completedFuture(null);
            }
            return CompletableFuture.supplyAsync(() -> getTpaRequestNames(player).toArray(String[]::new));
        }));
    }

    public void sendRequest(@NotNull Player target, @NotNull Player sender, @NotNull TPARequest.TPADirection direction) {
        if (target.getUniqueId() == sender.getUniqueId()) {
            TeleportConfig.getInstance().getTpaCannotRequestSelfMessage().sendMessage(sender);
            return;
        }
        List<TPARequest> targetTpaList = getTpaRequests(target);
        int size = targetTpaList.size();
        while (size >= TeleportConfig.getInstance().getTpaMaximumCachedRequests()) {
            targetTpaList.removeFirst();
            size = targetTpaList.size();
        }
        TPARequest request = new TPARequest(sender, target, direction);
        targetTpaList.add(request);
        Bukkit.getScheduler().runTaskLater(Firefly.getInstance(), () -> targetTpaList.remove(request), TeleportConfig.getInstance().getTpaRequestExpiry() * 20L);

        Component accept = TeleportConfig.getInstance().getTpaAcceptClickMessage(sender).getMessage();
        Component deny = TeleportConfig.getInstance().getTpaDenyClickMessage(sender).getMessage();

        ComponentReplacer acceptDenyReplacer = ComponentReplacer.componentReplacer(Map.of(
                "accept", accept,
                "deny", deny
        ));

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

    public void acceptRequest(@NotNull Player player, @NotNull String senderName) {
        Iterator<TPARequest> iterator = getTpaRequests(player).iterator();
        while (iterator.hasNext()) {
            TPARequest request = iterator.next();
            if (request.getSenderName().equals(senderName)) {
                request.accept();
                iterator.remove();
                return;
            }
        }
    }

    public void denyRequest(@NotNull Player player, @NotNull String senderName) {
        Iterator<TPARequest> iterator = getTpaRequests(player).iterator();
        while (iterator.hasNext()) {
            TPARequest request = iterator.next();
            if (request.getSenderName().equals(senderName)) {
                request.deny();
                iterator.remove();
                return;
            }
        }
    }

}
