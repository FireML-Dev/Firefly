package uk.firedev.firefly.modules.nickname;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.nickname.command.NicknameCommand;
import uk.firedev.firefly.placeholders.Placeholders;

import java.util.Objects;

public class NicknameModule implements Module {

    private static NicknameModule instance = null;

    public static final String COMMAND_PERMISSION = "firefly.command.nickname";
    public static final String COMMAND_LENGTH_BYPASS_PERMISSION = "firefly.command.nickname.bypass.length";
    public static final String COMMAND_BLACKLIST_BYPASS_PERMISSION = "firefly.command.nickname.bypass.blacklist";
    public static final String COMMAND_UNIQUE_PERMISSION = "firefly.command.nickname.unique";

    public static final String COMMAND_PERMISSION_ADMIN = "firefly.command.nickname.admin";

    private boolean loaded;

    private NicknameModule() {}

    public static NicknameModule getInstance() {
        if (instance == null) {
            instance = new NicknameModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "Nickname";
    }

    @Override
    public boolean isConfigEnabled() {
        return ModuleConfig.getInstance().isModuleEnabled("nicknames");
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        NicknameConfig.getInstance().init();
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Nickname Command");
        NicknameCommand.getCommand().register(Firefly.getInstance());
        NicknameDatabase.getInstance().register(Firefly.getInstance().getDatabase());
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        NicknameConfig.getInstance().reload();
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

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider ->
            provider.addAudiencePlaceholder("player_nickname", audience -> {
                if (!isLoaded()) {
                    return MessageConfig.getInstance().getFeatureDisabledMessage().toSingleMessage().get();
                }
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                if (isLoaded()) {
                    return getNickname(player);
                } else {
                    return player.name();
                }
            }));
    }

    // Nickname Management

    public Component getNickname(@NotNull OfflinePlayer player) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return Component.text(Objects.requireNonNullElse(player.getName(), "N/A"));
        }
        return data.getNickname();
    }

    public void setNickname(@NotNull OfflinePlayer player, @NotNull Component nickname) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setNickname(nickname);
    }

    public void setNickname(@NotNull OfflinePlayer player, @NotNull String nickname) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setRawNickname(nickname);
    }

    public void removeNickname(@NotNull OfflinePlayer player) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.removeNickname();
    }

}
