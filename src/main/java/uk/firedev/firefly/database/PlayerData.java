package uk.firedev.firefly.database;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.modules.nickname.NicknameConfig;
import uk.firedev.firefly.utils.StringUtils;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private long playtime = 0;
    private @Nullable String nickname = null;

    protected PlayerData(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Gets the username of this PlayerData's owner
     * @return The PlayerData owner's username, or N/A if it cannot be found.
     */
    public @NotNull String getUsername() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return Objects.requireNonNullElse(offlinePlayer.getName(), "N/A");
    }

    // Database Methods

    public void save() {
        Database database = Firefly.getInstance().getDatabase();
        database.getLoadedModules().forEach(module -> {
            if (module instanceof FireflyDatabaseModule fireflyModule) {
                try {
                    fireflyModule.save(this, database.getConnection());
                } catch (SQLException exception) {
                    Loggers.error(fireflyModule.getClass(), "Failed to save data", exception);
                }
            }
        });
    }

    // Playtime Methods

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public long getPlaytime() {
        return this.playtime;
    }

    // Nickname Methods

    public void setNickname(@NotNull Component nickname) {
        this.nickname = StringUtils.getColorOnlyMiniMessage().serialize(nickname);
    }

    public void setRawNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }

    public void removeNickname() {
        this.nickname = null;
    }

    public @NotNull Component getNickname() {
        String finalName = this.nickname;
        String username = getUsername();
        if (finalName == null || finalName.isEmpty()) {
            finalName = username;
        }
        Component component = StringUtils.getColorOnlyComponent(finalName);
        ComponentMessage componentMessage = ComponentMessage.of(component);
        if (!componentMessage.matchesString(username)) {
            component = component.hoverEvent(
                HoverEvent.hoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    NicknameConfig.getInstance().getRealNameHoverMessage().replace("username", username).getMessage()
                )
            );
        }
        return component;
    }

    public @Nullable String getRawNickname() {
        return this.nickname;
    }

}
