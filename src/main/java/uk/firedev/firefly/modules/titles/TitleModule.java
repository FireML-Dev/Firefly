package uk.firedev.firefly.modules.titles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.titles.command.PrefixCommand;
import uk.firedev.firefly.modules.titles.command.SuffixCommand;
import uk.firedev.firefly.modules.titles.objects.Prefix;
import uk.firedev.firefly.modules.titles.objects.Suffix;
import uk.firedev.firefly.modules.titles.placeholders.PlayerPrefixPlaceholder;
import uk.firedev.firefly.modules.titles.placeholders.PlayerSuffixPlaceholder;
import uk.firedev.firefly.placeholders.FireflyPlaceholders;
import uk.firedev.daisylib.messages.message.ComponentMessage;
import uk.firedev.daisylib.messages.message.ComponentSingleMessage;

import java.util.ArrayList;
import java.util.List;

public class TitleModule implements Module {

    private static TitleModule instance = null;

    private List<Prefix> prefixes = new ArrayList<>();
    private List<Suffix> suffixes = new ArrayList<>();

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
    public void init() {
        TitleDatabase.getInstance().register(Firefly.getInstance().getDatabase());

        this.prefixes = TitleConfig.getInstance().getPrefixesFromFile();
        new PrefixCommand().initCommand();

        this.suffixes = TitleConfig.getInstance().getSuffixesFromFile();
        new SuffixCommand().initCommand();

        registerPlaceholders();
    }

    @Override
    public void reload() {
        TitleConfig.getInstance().reload();
        this.prefixes = TitleConfig.getInstance().getPrefixesFromFile();
        this.suffixes = TitleConfig.getInstance().getSuffixesFromFile();
    }

    @Override
    public void unload() {
        this.prefixes = new ArrayList<>();
        this.suffixes = new ArrayList<>();
    }

    private void registerPlaceholders() {
        FireflyPlaceholders.get().add(new PlayerPrefixPlaceholder(this));
        FireflyPlaceholders.get().add(new PlayerSuffixPlaceholder(this));
    }

    public void setPlayerPrefix(@NonNull OfflinePlayer player, @NonNull String prefix) {
        setPlayerPrefix(player, ComponentMessage.componentMessage(prefix));
    }

    public void setPlayerPrefix(@NonNull OfflinePlayer player, @NonNull Prefix prefix) {
        setPlayerPrefix(player, prefix.getDisplay());
    }

    public void setPlayerPrefix(@NonNull OfflinePlayer player, @NonNull ComponentSingleMessage prefix) {
        if (!isConfigEnabled()) {
            return;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setPrefix(prefix);

        Player online = player.getPlayer();
        if (online != null) {
            TitleConfig.getInstance().getPrefixSetMessage()
                .replace("{new-prefix}", prefix.get())
                .send(online);
        }
    }

    public void removePlayerPrefix(@NonNull OfflinePlayer player) {
        if (!isConfigEnabled()) {
            return;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.removePrefix();

        Player online = player.getPlayer();
        if (online != null) {
            TitleConfig.getInstance().getPrefixRemovedMessage().send(online);
        }
    }

    public @Nullable Component getPlayerPrefix(@NonNull OfflinePlayer player) {
        if (!isConfigEnabled()) {
            return null;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null || data.getPrefix() == null) {
            return null;
        }
        return data.getPrefix().get();
    }

    public @Nullable String getPlayerPrefixLegacy(@NonNull OfflinePlayer player) {
        Component prefix = getPlayerPrefix(player);
        if (prefix == null) {
            return null;
        }
        return LegacyComponentSerializer.legacySection().serialize(prefix);
    }

    public void setPlayerSuffix(@NonNull OfflinePlayer player, @NonNull String suffix) {
        setPlayerSuffix(player, ComponentMessage.componentMessage(suffix));
    }

    public void setPlayerSuffix(@NonNull OfflinePlayer player, @NonNull Suffix suffix) {
        setPlayerSuffix(player, suffix.getDisplay());
    }

    public void setPlayerSuffix(@NonNull OfflinePlayer player, @NonNull ComponentSingleMessage suffix) {
        if (!isConfigEnabled()) {
            return;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setSuffix(suffix);

        Player online = player.getPlayer();
        if (online != null) {
            TitleConfig.getInstance().getSuffixSetMessage()
                .replace("{new-suffix}", suffix.get())
                .send(online);
        }
    }

    public void removePlayerSuffix(@NonNull OfflinePlayer player) {
        if (!isConfigEnabled()) {
            return;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.removeSuffix();

        Player online = player.getPlayer();
        if (online != null) {
            TitleConfig.getInstance().getSuffixRemovedMessage().send(online);
        }
    }

    public @Nullable Component getPlayerSuffix(@NonNull OfflinePlayer player) {
        if (!isConfigEnabled()) {
            return null;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null || data.getSuffix() == null) {
            return null;
        }
        return data.getSuffix().get();
    }

    public @Nullable String getPlayerSuffixLegacy(@NonNull OfflinePlayer player) {
        Component suffix = getPlayerSuffix(player);
        if (suffix == null) {
            return null;
        }
        return LegacyComponentSerializer.legacySection().serialize(suffix);
    }

    public List<Prefix> getPrefixes() {
        if (!isConfigEnabled()) {
            return List.of();
        }
        if (prefixes == null || prefixes.isEmpty()) {
            this.prefixes = TitleConfig.getInstance().getPrefixesFromFile();
        }
        return prefixes;
    }

    public List<Suffix> getSuffixes() {
        if (!isConfigEnabled()) {
            return List.of();
        }
        if (suffixes == null || suffixes.isEmpty()) {
            this.suffixes = TitleConfig.getInstance().getSuffixesFromFile();
        }
        return suffixes;
    }

}
