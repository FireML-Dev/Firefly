package uk.firedev.firefly.database;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MainConfig;
import uk.firedev.firefly.modules.nickname.NicknameConfig;
import uk.firedev.firefly.utils.StringUtils;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;

    private Instant unloadInstant;
    private long playtime = 0;
    private @Nullable String nickname = null;

    protected PlayerData(@NotNull UUID uuid) {
        this.uuid = uuid;
        markAccessed();
    }

    private void markAccessed() {
        this.unloadInstant = Instant.now().plus(MainConfig.getInstance().getDatabaseCacheInterval());
    }

    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Gets the username of this PlayerData's owner
     * @return The PlayerData owner's username, or N/A if it cannot be found.
     */
    public @NotNull String getUsername() {
        markAccessed();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return Objects.requireNonNullElse(offlinePlayer.getName(), "N/A");
    }

    // Database Methods

    public boolean canUnload() {
        Player onlinePlayer = Bukkit.getPlayer(uuid);
        return onlinePlayer == null && unloadInstant.isBefore(Instant.now());
    }

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
        if (canUnload()) {
            Firefly.getInstance().getDatabase().unloadPlayerData(uuid);
        }
    }

    // Playtime Methods

    public void setPlaytime(long playtime) {
        markAccessed();
        this.playtime = playtime;
    }

    public long getPlaytime() {
        markAccessed();
        return this.playtime;
    }

    // Nickname Methods

    public void setNickname(@NotNull Component nickname) {
        markAccessed();
        this.nickname = StringUtils.getColorOnlyMiniMessage().serialize(nickname);
    }

    public void setRawNickname(@NotNull String nickname) {
        markAccessed();
        this.nickname = nickname;
    }

    public void removeNickname() {
        markAccessed();
        this.nickname = null;
    }

    public @NotNull Component getNickname() {
        markAccessed();
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
        markAccessed();
        return this.nickname;
    }

}
