package uk.firedev.firefly.modules.nickname;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.nickname.command.NicknameAdminCommand;
import uk.firedev.firefly.modules.nickname.command.NicknameCheckCommand;
import uk.firedev.firefly.modules.nickname.command.NicknameCommand;
import uk.firedev.firefly.placeholders.Placeholders;
import uk.firedev.firefly.utils.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NicknameModule implements Module {

    private static NicknameModule instance = null;

    private boolean loaded;
    private final Map<UUID, String> nicknameMap;

    private NicknameModule() {
        nicknameMap = new ConcurrentHashMap<>();
    }

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
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Nickname Commands");
        NicknameCommand.getCommand().register(Firefly.getInstance());
        NicknameAdminCommand.getCommand().register(Firefly.getInstance());
        NicknameCheckCommand.getCommand().register(Firefly.getInstance());
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
        Placeholders.manageProvider(provider -> {
            provider.addAudiencePlaceholder("player_nickname", audience -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                if (isLoaded()) {
                    return getNickname(player);
                } else {
                    return player.name();
                }
            });
        });
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
