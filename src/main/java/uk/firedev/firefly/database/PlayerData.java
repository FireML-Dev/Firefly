package uk.firedev.firefly.database;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private @Nullable ComponentMessage prefix = null;
    private @Nullable ComponentMessage suffix = null;
    private @Nullable Location lastTeleportLocation = null;

    protected PlayerData(@NotNull UUID uuid) {
        this.uuid = uuid;
        markAccessed();
    }

    public void markAccessed() {
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
        return onlinePlayer == null && Instant.now().isAfter(unloadInstant);
    }

    public void save() {
        Instant unloadInstant = this.unloadInstant;
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
        this.unloadInstant = unloadInstant;
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

    // Title Methods

    public @Nullable ComponentMessage getPrefix() {
        markAccessed();
        return this.prefix;
    }

    public void setPrefix(@NotNull ComponentMessage prefix) {
        markAccessed();
        this.prefix = prefix;
    }

    public void removePrefix() {
        markAccessed();
        this.prefix = null;
    }

    public @Nullable ComponentMessage getSuffix() {
        markAccessed();
        return this.suffix;
    }

    public void setSuffix(@NotNull ComponentMessage suffix) {
        markAccessed();
        this.suffix = suffix;
    }

    public void removeSuffix() {
        markAccessed();
        this.suffix = null;
    }

    // Teleport Methods

    public @Nullable Location getLastTeleportLocation() {
        return lastTeleportLocation;
    }

    public void setLastTeleportLocation(@NotNull Location lastTeleportLocation) {
        this.lastTeleportLocation = lastTeleportLocation;
    }

    public void removeLastTeleportLocation() {
        this.lastTeleportLocation = null;
    }

}
