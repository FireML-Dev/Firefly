package uk.firedev.firefly.modules.nickname;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.nickname.command.NicknameCommand;
import uk.firedev.firefly.modules.nickname.placeholders.PlayerNicknamePlaceholder;
import uk.firedev.firefly.placeholders.FireflyPlaceholders;

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
        NicknameDatabase.getInstance().register(Firefly.getInstance().getDatabase());
        new NicknameCommand().initCommand();
        registerPlaceholders();
    }

    @Override
    public void reload() {
        NicknameConfig.getInstance().reload();
    }

    @Override
    public void unload() {
        Bukkit.getOnlinePlayers().forEach(player -> player.displayName(null));
    }

    private void registerPlaceholders() {
        FireflyPlaceholders.get().add(new PlayerNicknamePlaceholder(this));
    }

    // Nickname Management

    public Component getNickname(@NonNull OfflinePlayer player) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (!isConfigEnabled() || data == null) {
            String name = player.getName();
            return Component.text(name == null ? "N/A" : name);
        }
        return data.getNickname();
    }

    public void setNickname(@NonNull OfflinePlayer player, @NonNull Component nickname) {
        if (!isConfigEnabled()) {
            return;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setNickname(nickname);
    }

    public void setNickname(@NonNull OfflinePlayer player, @NonNull String nickname) {
        if (!isConfigEnabled()) {
            return;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setRawNickname(nickname);
    }

    public void removeNickname(@NonNull OfflinePlayer player) {
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
