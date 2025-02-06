package uk.firedev.firefly.modules.titles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.titles.command.PrefixCommand;
import uk.firedev.firefly.modules.titles.command.SuffixCommand;
import uk.firedev.firefly.modules.titles.objects.Prefix;
import uk.firedev.firefly.modules.titles.objects.Suffix;
import uk.firedev.firefly.placeholders.Placeholders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TitleModule implements Module {

    private static TitleModule instance = null;

    private List<Prefix> prefixes = new ArrayList<>();
    private List<Suffix> suffixes = new ArrayList<>();
    private boolean loaded = false;

    private TitleModule() {}

    public static TitleModule getInstance() {
        if (instance == null) {
            instance = new TitleModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "Title";
    }

    @Override
    public boolean isConfigEnabled() {
        return ModuleConfig.getInstance().isModuleEnabled("titles");
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        if (VaultManager.getInstance().getChat() == null) {
            Loggers.warn(Firefly.getInstance().getComponentLogger(), "The Title Module cannot load because there is no Vault Chat manager detected. Please register one to use this module! (Something like LuckPerms should be fine)");
            return;
        }
        TitleConfig.getInstance().init();
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Title Commands");
        PrefixCommand.getInstance().register(Firefly.getInstance());
        SuffixCommand.getInstance().register(Firefly.getInstance());
        this.prefixes = TitleConfig.getInstance().getPrefixesFromFile();
        this.suffixes = TitleConfig.getInstance().getSuffixesFromFile();
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        TitleConfig.getInstance().reload();
        this.prefixes = TitleConfig.getInstance().getPrefixesFromFile();
        this.suffixes = TitleConfig.getInstance().getSuffixesFromFile();
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        this.prefixes = new ArrayList<>();
        this.suffixes = new ArrayList<>();
        loaded = false;
    }

    @Override
    public boolean isLoaded() { return loaded; }

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider -> {
            provider.addAudiencePlaceholder("player_prefix", audience -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                if (TitleModule.getInstance().isLoaded()) {
                    return TitleModule.getInstance().getPlayerPrefix(player);
                } else {
                    String prefix = Objects.requireNonNull(VaultManager.getInstance().getChat()).getPlayerPrefix(player);
                    return ComponentMessage.fromString(prefix).getMessage();
                }
            });
            provider.addAudiencePlaceholder("player_suffix", audience -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                if (TitleModule.getInstance().isLoaded()) {
                    return TitleModule.getInstance().getPlayerSuffix(player);
                } else {
                    String prefix = Objects.requireNonNull(VaultManager.getInstance().getChat()).getPlayerSuffix(player);
                    return ComponentMessage.fromString(prefix).getMessage();
                }
            });
        });
    }

    public NamespacedKey getPrefixKey() {
        return new NamespacedKey(Firefly.getInstance(), "player-prefix");
    }

    public NamespacedKey getSuffixKey() {
        return new NamespacedKey(Firefly.getInstance(), "player-suffix");
    }

    public void setPlayerPrefix(@NotNull Player player, @NotNull String prefix) {
        setPlayerPrefix(player, ComponentMessage.fromString(prefix).getMessage());
    }

    public void setPlayerPrefix(@NotNull Player player, @NotNull Prefix prefix) {
        setPlayerPrefix(player, prefix.getDisplay().getMessage());
    }

    public void setPlayerPrefix(@NotNull Player player, @NotNull Component prefix) {
        String stringPrefix = ComponentMessage.of(prefix).toStringMessage().getMessage();
        player.getPersistentDataContainer().set(getPrefixKey(), PersistentDataType.STRING, stringPrefix);
        ComponentReplacer replacer = ComponentReplacer.create("new-prefix", prefix);
        TitleConfig.getInstance().getPrefixSetMessage().applyReplacer(replacer).sendMessage(player);
    }

    public void removePlayerPrefix(@NotNull Player player) {
        player.getPersistentDataContainer().remove(getPrefixKey());
        TitleConfig.getInstance().getPrefixRemovedMessage().sendMessage(player);
    }

    public Component getPlayerPrefix(@NotNull Player player) {
        String prefix = player.getPersistentDataContainer().get(
                getPrefixKey(), PersistentDataType.STRING
        );
        if (prefix == null) {
            if (VaultManager.getInstance().getChat() == null) {
                return Component.empty();
            } else {
                // Horrible but necessary color parsing code
                String vaultPrefix = VaultManager.getInstance().getChat().getPlayerPrefix(player);
                Component componentPrefix;
                try {
                    componentPrefix = MiniMessage.miniMessage().deserialize(vaultPrefix);
                } catch (ParsingException exception) {
                    componentPrefix = LegacyComponentSerializer.legacySection().deserialize(vaultPrefix);
                }
                return componentPrefix;
            }
        }
        return ComponentMessage.fromString(prefix).getMessage();
    }

    public String getPlayerPrefixLegacy(@NotNull Player player) {
        return LegacyComponentSerializer.legacySection().serialize(getPlayerPrefix(player));
    }

    public void setPlayerSuffix(@NotNull Player player, @NotNull String suffix) {
        setPlayerSuffix(player, ComponentMessage.fromString(suffix).getMessage());
    }

    public void setPlayerSuffix(@NotNull Player player, @NotNull Suffix suffix) {
        setPlayerSuffix(player, suffix.getDisplay().getMessage());
    }

    public void setPlayerSuffix(@NotNull Player player, @NotNull Component suffix) {
        String stringSuffix = ComponentMessage.of(suffix).toStringMessage().getMessage();
        player.getPersistentDataContainer().set(getSuffixKey(), PersistentDataType.STRING, stringSuffix);
        ComponentReplacer replacer = ComponentReplacer.create("new-suffix", suffix);
        TitleConfig.getInstance().getSuffixSetMessage().applyReplacer(replacer).sendMessage(player);
    }

    public void removePlayerSuffix(@NotNull Player player) {
        player.getPersistentDataContainer().remove(getSuffixKey());
        TitleConfig.getInstance().getSuffixRemovedMessage().sendMessage(player);
    }

    public Component getPlayerSuffix(@NotNull Player player) {
        String suffix = player.getPersistentDataContainer().get(
                getSuffixKey(), PersistentDataType.STRING
        );
        if (suffix == null) {
            if (VaultManager.getInstance().getChat() == null) {
                return Component.empty();
            } else {
                // Horrible but necessary color parsing code
                String vaultSuffix = VaultManager.getInstance().getChat().getPlayerSuffix(player);
                Component componentSuffix;
                try {
                    componentSuffix = MiniMessage.miniMessage().deserialize(vaultSuffix);
                } catch (ParsingException exception) {
                    componentSuffix = LegacyComponentSerializer.legacySection().deserialize(vaultSuffix);
                }
                return componentSuffix;
            }
        }
        return ComponentMessage.fromString(suffix).getMessage();
    }

    public String getPlayerSuffixLegacy(@NotNull Player player) {
        return LegacyComponentSerializer.legacySection().serialize(getPlayerSuffix(player));
    }

    public List<Prefix> getPrefixes() {
        if (prefixes == null || prefixes.isEmpty()) {
            this.prefixes = TitleConfig.getInstance().getPrefixesFromFile();
        }
        return prefixes;
    }

    public List<Suffix> getSuffixes() {
        if (suffixes == null || suffixes.isEmpty()) {
            this.suffixes = TitleConfig.getInstance().getSuffixesFromFile();
        }
        return suffixes;
    }

}
