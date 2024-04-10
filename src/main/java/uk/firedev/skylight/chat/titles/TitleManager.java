package uk.firedev.skylight.chat.titles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.utils.ComponentUtils;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.chat.titles.objects.Prefix;
import uk.firedev.skylight.chat.titles.objects.Suffix;
import uk.firedev.skylight.config.MessageConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TitleManager extends uk.firedev.daisylib.Config {

    private static TitleManager instance = null;

    private List<Prefix> prefixes = new ArrayList<>();
    private List<Suffix> suffixes = new ArrayList<>();
    private boolean loaded = false;

    private TitleManager() {
        super("titles.yml", Skylight.getInstance(), false);
    }

    public static TitleManager getInstance() {
        if (instance == null) {
            instance = new TitleManager();
        }
        return instance;
    }

    public void load() {
        if (VaultManager.getChat() == null) {
            Loggers.warning(Skylight.getInstance().getLogger(), "The Title Module cannot load when vault is disabled in DaisyLib's config. Please enable it to use this module!");
            return;
        }
        PrefixCommand.getInstance().register();
        SuffixCommand.getInstance().register();
        getPrefixesFromFile();
        getSuffixesFromFile();
        loaded = true;
    }

    @Override
    public void reload() {
        super.reload();
        getPrefixesFromFile();
        getSuffixesFromFile();
    }

    public boolean isLoaded() { return loaded; }

    public NamespacedKey getPrefixKey() {
        return ObjectUtils.createNamespacedKey("player-prefix", Skylight.getInstance());
    }

    public NamespacedKey getSuffixKey() {
        return ObjectUtils.createNamespacedKey("player-suffix", Skylight.getInstance());
    }

    public void setPlayerPrefix(@NotNull Player player, Prefix prefix) {
        Component prefixComponent = prefix.getDisplay();
        player.getPersistentDataContainer().set(getPrefixKey(), PersistentDataType.STRING, ComponentUtils.toString(prefixComponent));
        Component component = ComponentUtils.parseComponent(MessageConfig.getInstance().fromConfig("messages.title.prefix-set"),
                Map.of("prefix", prefixComponent)
        );
        player.sendMessage(component);
    }

    public void setPlayerPrefix(@NotNull Player player, Component prefix) {
        if (prefix == null) {
            removePlayerPrefix(player);
        } else {
            player.getPersistentDataContainer().set(getPrefixKey(), PersistentDataType.STRING, ComponentUtils.toString(prefix));
            Component component = ComponentUtils.parseComponent(MessageConfig.getInstance().fromConfig("messages.title.prefix-set"),
                    Map.of("prefix", prefix)
            );
            player.sendMessage(component);
        }
    }

    public void removePlayerPrefix(@NotNull Player player) {
        player.getPersistentDataContainer().remove(getPrefixKey());
        MessageConfig.getInstance().sendMessageFromConfig(player, "messages.title.prefix-removed");
    }

    public Component getPlayerPrefix(@NotNull Player player) {
        String prefix = player.getPersistentDataContainer().getOrDefault(
                getPrefixKey(), PersistentDataType.STRING, VaultManager.getChat().getPlayerPrefix(player)
        );
        return ComponentUtils.parseComponent(prefix);
    }

    public String getPlayerPrefixLegacy(@NotNull Player player) {
        return LegacyComponentSerializer.legacySection().serialize(getPlayerPrefix(player)).replace('&', '§');
    }

    public void setPlayerSuffix(@NotNull Player player, Suffix suffix) {
        Component suffixComponent = suffix.getDisplay();
        player.getPersistentDataContainer().set(getSuffixKey(), PersistentDataType.STRING, ComponentUtils.toString(suffixComponent));
        Component component = ComponentUtils.parseComponent(MessageConfig.getInstance().fromConfig("messages.title.suffix-set"),
                Map.of("suffix", suffixComponent)
        );
        player.sendMessage(component);
    }

    public void setPlayerSuffix(@NotNull Player player, Component suffix) {
        if (suffix == null) {
            removePlayerSuffix(player);
        } else {
            player.getPersistentDataContainer().set(getSuffixKey(), PersistentDataType.STRING, ComponentUtils.toString(suffix));
            Component component = ComponentUtils.parseComponent(MessageConfig.getInstance().fromConfig("messages.title.suffix-set"),
                    Map.of("suffix", suffix)
            );
            player.sendMessage(component);
        }
    }

    public void removePlayerSuffix(@NotNull Player player) {
        player.getPersistentDataContainer().remove(getSuffixKey());
        MessageConfig.getInstance().sendMessageFromConfig(player, "messages.title.suffix-removed");
    }

    public Component getPlayerSuffix(@NotNull Player player) {
        String suffix = player.getPersistentDataContainer().getOrDefault(
                getSuffixKey(), PersistentDataType.STRING, VaultManager.getChat().getPlayerSuffix(player)
        );
        return ComponentUtils.parseComponent(suffix);
    }

    public String getPlayerSuffixLegacy(@NotNull Player player) {
        return LegacyComponentSerializer.legacySection().serialize(getPlayerSuffix(player)).replace('&', '§');
    }

    public List<Prefix> getPrefixesFromFile() {
        ConfigurationSection section = getConfig().getConfigurationSection("titles.prefixes");
        if (section == null) {
            return List.of();
        }
        List<Prefix> prefixes = new ArrayList<>();
        section.getKeys(false).stream().map(section::getConfigurationSection).filter(Objects::nonNull).forEach(prefixSection -> {
            try {
                prefixes.add(new Prefix(prefixSection));
            } catch (InvalidConfigurationException ex) {
                Loggers.logException(ex, Skylight.getInstance().getLogger());
            }
        });
        this.prefixes = prefixes;
        return prefixes;
    }

    public List<Prefix> getPrefixes() {
        if (prefixes.isEmpty()) {
            return getPrefixesFromFile();
        }
        return prefixes;
    }

    public List<Suffix> getSuffixesFromFile() {
        ConfigurationSection section = getConfig().getConfigurationSection("titles.suffixes");
        if (section == null) {
            return List.of();
        }
        List<Suffix> suffixes = new ArrayList<>();
        section.getKeys(false).stream().map(section::getConfigurationSection).filter(Objects::nonNull).forEach(prefixSection -> {
            try {
                suffixes.add(new Suffix(prefixSection));
            } catch (InvalidConfigurationException ex) {
                Loggers.logException(ex, Skylight.getInstance().getLogger());
            }
        });
        this.suffixes = suffixes;
        return suffixes;
    }

    public List<Suffix> getSuffixes() {
        if (suffixes.isEmpty()) {
            return getSuffixesFromFile();
        }
        return suffixes;
    }

}
