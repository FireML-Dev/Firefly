package uk.firedev.skylight.chat.titles;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.VaultManager;

public class TitleManager {

    private static TitleManager instance = null;

    private TitleManager() {}

    public static TitleManager getInstance() {
        if (instance == null) {
            instance = new TitleManager();
        }
        return instance;
    }

    public void load() {
        new TitleCommand().registerCommand("title", Skylight.getInstance());
    }

    public NamespacedKey getPrefixKey() {
        return ObjectUtils.createNamespacedKey("player-prefix", Skylight.getInstance());
    }

    public NamespacedKey getSuffixKey() {
        return ObjectUtils.createNamespacedKey("player-prefix", Skylight.getInstance());
    }

    public void setPlayerPrefix(@NotNull Player player, String prefix) {
        if (prefix == null || MiniMessage.miniMessage().stripTags(prefix).isEmpty()) {
            player.getPersistentDataContainer().remove(getPrefixKey());
        } else {
            player.getPersistentDataContainer().set(getPrefixKey(), PersistentDataType.STRING, prefix);
        }
    }

    public String getPlayerPrefix(@NotNull Player player) {
        return player.getPersistentDataContainer().getOrDefault(
                getPrefixKey(), PersistentDataType.STRING, VaultManager.getChat().getPlayerPrefix(player)
        );
    }

    public void setPlayerSuffix(@NotNull Player player, String suffix) {
        if (suffix == null || MiniMessage.miniMessage().stripTags(suffix).isEmpty()) {
            player.getPersistentDataContainer().remove(getSuffixKey());
        } else {
            player.getPersistentDataContainer().set(getSuffixKey(), PersistentDataType.STRING, suffix);
        }
    }

    public String getPlayerSuffix(@NotNull Player player) {
        return player.getPersistentDataContainer().getOrDefault(
                getSuffixKey(), PersistentDataType.STRING, VaultManager.getChat().getPlayerSuffix(player)
        );
    }

}
