package uk.firedev.skylight.modules.nickname;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.config.MessageConfig;
import uk.firedev.skylight.database.Database;
import uk.firedev.skylight.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NicknameManager {

    private static NicknameManager instance = null;

    private boolean loaded;
    private Map<UUID, String> nicknameMap = new HashMap<>();

    private NicknameManager() {}

    public static NicknameManager getInstance() {
        if (instance == null) {
            instance = new NicknameManager();
        }
        return instance;
    }

    public void load() {
        if (isLoaded()) {
            return;
        }
        loaded = true;
        NicknameCommand.getInstance().register();
        NicknameAdminCommand.getInstance().register();
        NicknameCheckCommand.getInstance().register();
        populateNicknameMap();
    }

    public void unload() {
        if (!isLoaded()) {
            return;
        }
        loaded = false;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public NamespacedKey getNicknameKey() {
        return ObjectUtils.createNamespacedKey("nickname", Skylight.getInstance());
    }

    // Nickname Management

    /**
     * Gets a player's nickname as an Adventure Component
     * @param player The player whose nickname to retrieve
     * @return The player's nickname as an Adventure Component
     */
    public Component getNickname(@NotNull OfflinePlayer player) {
        String savedName = getNicknameMap().get(player.getUniqueId());
        if (savedName == null || savedName.isEmpty()) {
            savedName = Objects.requireNonNull(player.getName());
        }
        Component component = StringUtils.convertLegacyToAdventure(savedName);
        ComponentMessage componentMessage = new ComponentMessage(component);
        if (componentMessage.matchesString(Objects.requireNonNull(player.getName()))) {
            ComponentReplacer replacer = new ComponentReplacer().addReplacements("username", player.getName());
            component = component.hoverEvent(
                    HoverEvent.hoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            MessageConfig.getInstance().getNicknameRealHoverMessage().applyReplacer(replacer).getMessage()
                    )
            );
        }
        return component;
    }

    /**
     * Gets a player's nickname as a Legacy String
     * @param player The player whose nickname to retrieve
     * @return The player's nickname as a Legacy String
     */
    public String getStringNickname(@NotNull OfflinePlayer player) {
        return LegacyComponentSerializer.legacySection().serialize(getNickname(player));
    }

    /**
     * Sets a player's nickname via an Adventure Component
     * @param player The player to set
     * @param nickname The nickname to set, as an Adventure Component
     */
    public CompletableFuture<Void> setNickname(@NotNull OfflinePlayer player, @NotNull Component nickname) {
        return setStringNickname(player, LegacyComponentSerializer.legacySection().serialize(nickname));
    }

    /**
     * Sets a player's nickname via a Legacy String
     * @param player The player to set
     * @param nickname The nickname to set, as a Legacy String
     */
    public CompletableFuture<Void> setStringNickname(@NotNull OfflinePlayer player, @NotNull String nickname) {
        return Database.getInstance().setNickname(player.getUniqueId(), nickname);
    }

    /**
     * Removes a player's custom nickname.
     * @param player The player whose nickname to remove.
     */
    public void removeNickname(@NotNull OfflinePlayer player) {
        Database.getInstance().setNickname(player.getUniqueId(), "");
    }

    public void populateNicknameMap() {
        if (isLoaded()) {
            Database.getInstance().getNicknames().thenAccept(map -> nicknameMap = new HashMap<>(map));
        }
    }

    public @NotNull Map<UUID, String> getNicknameMap() {
        if (nicknameMap == null) {
            nicknameMap = new HashMap<>();
        }
        return Map.copyOf(nicknameMap);
    }

}
