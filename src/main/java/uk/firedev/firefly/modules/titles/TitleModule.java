package uk.firedev.firefly.modules.titles;

import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault2.chat.Chat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.titles.command.PrefixCommand;
import uk.firedev.firefly.modules.titles.command.SuffixCommand;
import uk.firedev.firefly.modules.titles.objects.Prefix;
import uk.firedev.firefly.modules.titles.objects.Suffix;
import uk.firedev.firefly.placeholders.Placeholders;
import uk.firedev.firefly.utils.StringUtils;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

import java.util.ArrayList;
import java.util.List;

public class TitleModule implements Module {

    private static TitleModule instance = null;

    private Chat chat;
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
        Chat chat = VaultManager.getInstance().getChat();
        if (chat == null) {
            Loggers.warn(Firefly.getInstance().getComponentLogger(), "The Title Module cannot load because there is no Vault Chat manager detected. Please install LuckPerms to resolve this!");
            return;
        }
        this.chat = chat;
        TitleConfig.getInstance().init();
        TitleDatabase.getInstance().register(Firefly.getInstance().getDatabase());

        this.prefixes = TitleConfig.getInstance().getPrefixesFromFile();
        this.suffixes = TitleConfig.getInstance().getSuffixesFromFile();
    }

    @Override
    public void registerCommands(@NotNull Commands registrar) {
        registrar.register(new PrefixCommand().get());
        registrar.register(new SuffixCommand().get());
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

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider -> {
            provider.addAudiencePlaceholder("player_prefix", audience -> {
                if (!isConfigEnabled()) {
                    return MessageConfig.getInstance().getFeatureDisabledMessage().toSingleMessage().get();
                }
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                return getPlayerPrefix(player);
            });
            provider.addAudiencePlaceholder("player_suffix", audience -> {
                if (!isConfigEnabled()) {
                    return MessageConfig.getInstance().getFeatureDisabledMessage().toSingleMessage().get();
                }
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                return TitleModule.getInstance().getPlayerSuffix(player);
            });
        });
    }

    public void setPlayerPrefix(@NotNull Player player, @NotNull String prefix) {
        setPlayerPrefix(player, ComponentMessage.componentMessage(prefix));
    }

    public void setPlayerPrefix(@NotNull Player player, @NotNull Prefix prefix) {
        setPlayerPrefix(player, prefix.getDisplay());
    }

    public void setPlayerPrefix(@NotNull Player player, @NotNull ComponentSingleMessage prefix) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setPrefix(prefix);
        TitleConfig.getInstance().getPrefixSetMessage()
            .replace("{new-prefix}", prefix.get())
            .send(player);
    }

    public void removePlayerPrefix(@NotNull Player player) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.removePrefix();
        TitleConfig.getInstance().getPrefixRemovedMessage().send(player);
    }

    public @Nullable Component getPlayerPrefix(@NotNull Player player) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null || data.getPrefix() == null) {
            if (chat == null) {
                return null;
            }
            String vaultPrefix = chat.getPlayerPrefix(player);
            return ComponentMessage.componentMessage(vaultPrefix).get();
        }
        return data.getPrefix().get();
    }

    public @Nullable String getPlayerPrefixLegacy(@NotNull Player player) {
        Component prefix = getPlayerPrefix(player);
        if (prefix == null) {
            return null;
        }
        return LegacyComponentSerializer.legacySection().serialize(prefix);
    }

    public void setPlayerSuffix(@NotNull Player player, @NotNull String suffix) {
        setPlayerSuffix(player, ComponentMessage.componentMessage(suffix));
    }

    public void setPlayerSuffix(@NotNull Player player, @NotNull Suffix suffix) {
        setPlayerSuffix(player, suffix.getDisplay());
    }

    public void setPlayerSuffix(@NotNull Player player, @NotNull ComponentSingleMessage suffix) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setSuffix(suffix);
        TitleConfig.getInstance().getSuffixSetMessage()
            .replace("{new-suffix}", suffix.get())
            .send(player);
    }

    public void removePlayerSuffix(@NotNull Player player) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.removeSuffix();
        TitleConfig.getInstance().getSuffixRemovedMessage().send(player);
    }

    public @Nullable Component getPlayerSuffix(@NotNull Player player) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null || data.getSuffix() == null) {
            if (chat == null) {
                return null;
            }
            String vaultSuffix = chat.getPlayerSuffix(player);
            return ComponentMessage.componentMessage(vaultSuffix).get();
        }
        return data.getSuffix().get();
    }

    public @Nullable String getPlayerSuffixLegacy(@NotNull Player player) {
        Component suffix = getPlayerSuffix(player);
        if (suffix == null) {
            return null;
        }
        return LegacyComponentSerializer.legacySection().serialize(suffix);
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
