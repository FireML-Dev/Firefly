package uk.firedev.firefly.modules.messaging;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.messaging.command.MessageCommand;
import uk.firedev.firefly.modules.messaging.command.ReplyCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessagingModule implements Module {

    private static MessagingModule instance;
    public static final String MESSAGE_PERMISSION = "firefly.command.message";
    public static final String REPLY_PERMISSION = "firefly.command.reply";

    private boolean loaded = false;
    private final Map<UUID, UUID> lastMessages = new HashMap<>();

    private MessagingModule() {}

    public static MessagingModule getInstance() {
        if (instance == null) {
            instance = new MessagingModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "Messaging";
    }

    @Override
    public boolean isConfigEnabled() {
        return ModuleConfig.getInstance().isModuleEnabled("messaging");
    }

    @Override
    public void init() {
        if (isLoaded()) {
            return;
        }
        MessagingConfig.getInstance().init();
        MessageCommand.getCommand().register(Firefly.getInstance());
        ReplyCommand.getCommand().register(Firefly.getInstance());
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        MessagingConfig.getInstance().reload();
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public void setLastMessage(@NotNull UUID receiver, @NotNull UUID sender) {
        lastMessages.put(receiver, sender);
    }

    public @Nullable Player getLastMessage(@NotNull UUID sender) {
        UUID uuid = lastMessages.get(sender);
        if (uuid == null) {
            return null;
        }
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) {
            return null;
        }
        return player;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastMessages.remove(event.getPlayer().getUniqueId());
    }

}
