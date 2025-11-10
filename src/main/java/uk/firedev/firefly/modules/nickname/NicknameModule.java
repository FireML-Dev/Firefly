package uk.firedev.firefly.modules.nickname;

import io.papermc.paper.command.brigadier.Commands;
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
    public void init() {
        NicknameConfig.getInstance().init();
        NicknameDatabase.getInstance().register(Firefly.getInstance().getDatabase());
        new NicknameCommand().initCommand();
    }

    @Override
    public void reload() {
        NicknameConfig.getInstance().reload();
    }

    @Override
    public void unload() {}

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider ->
            provider.addAudiencePlaceholder("player_nickname", audience -> {
                if (!isConfigEnabled()) {
                    return MessageConfig.getInstance().getFeatureDisabledMessage().toSingleMessage().get();
                }
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                return getNickname(player);
            }));
    }

    // Nickname Management

    public Component getNickname(@NotNull OfflinePlayer player) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (!isConfigEnabled() || data == null) {
            String name = player.getName();
            return Component.text(name == null ? "N/A" : name);
        }
        return data.getNickname();
    }

    public void setNickname(@NotNull OfflinePlayer player, @NotNull Component nickname) {
        if (!isConfigEnabled()) {
            return;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setNickname(nickname);
    }

    public void setNickname(@NotNull OfflinePlayer player, @NotNull String nickname) {
        if (!isConfigEnabled()) {
            return;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setRawNickname(nickname);
    }

    public void removeNickname(@NotNull OfflinePlayer player) {
        if (!isConfigEnabled()) {
            return;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.removeNickname();
    }

}
