package uk.firedev.skylight.modules.nickname;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.utils.ComponentUtils;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.config.MessageConfig;
import uk.firedev.skylight.utils.StringUtils;

public class NicknameManager {

    private static NicknameManager instance = null;

    private boolean loaded;

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
        NicknameCommand.getInstance().register();
        loaded = true;
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
     * @return The player's nickname as a Legacy String
     */
    public Component getNickname(@NotNull OfflinePlayer offlinePlayer) {
        Player player = offlinePlayer.getPlayer();
        if (player != null) {
            String savedName = player.getPersistentDataContainer().getOrDefault(getNicknameKey(), PersistentDataType.STRING, player.getName());
            Component component = StringUtils.convertLegacyToAdventure(savedName);
            if (!ComponentUtils.toUncoloredString(component).equals(player.getName())) {
                String message = MessageConfig.getInstance().getConfig().getString("messages.nicknames.real-name-hover", "<color:#F0E68C>Real Username:</color> <white>{username}</white>");
                component = component.hoverEvent(
                        HoverEvent.hoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                ComponentUtils.parseComponent(message, "username", player.getName())
                        )
                );
            }
            return component;
        }
        return ComponentUtils.parseComponent(offlinePlayer.getName());
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
    public void setNickname(@NotNull Player player, @NotNull Component nickname) {
        setStringNickname(player, LegacyComponentSerializer.legacySection().serialize(nickname));
    }

    /**
     * Sets a player's nickname via a Legacy String
     * @param player The player to set
     * @param nickname The nickname to set, as a Legacy String
     */
    public void setStringNickname(@NotNull Player player, @NotNull String nickname) {
        player.getPersistentDataContainer().set(getNicknameKey(), PersistentDataType.STRING, nickname);
    }

    /**
     * Removes a player's custom nickname.
     * @param player The player whose nickname to remove.
     */
    public void removeNickname(@NotNull Player player) {
        player.getPersistentDataContainer().remove(getNicknameKey());
    }

}
