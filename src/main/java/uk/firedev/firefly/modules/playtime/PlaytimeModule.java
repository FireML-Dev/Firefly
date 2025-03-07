package uk.firedev.firefly.modules.playtime;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.utils.DurationFormatter;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.playtime.command.PlaytimeCommand;
import uk.firedev.firefly.placeholders.Placeholders;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlaytimeModule implements Module {

    private static PlaytimeModule instance;

    private boolean loaded = false;
    private BukkitTask playtimeTask = null;

    private PlaytimeModule() {}

    public static PlaytimeModule getInstance() {
        if (instance == null) {
            instance = new PlaytimeModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "Playtime";
    }

    @Override
    public boolean isConfigEnabled() {
        return ModuleConfig.getInstance().isModuleEnabled("playtime");
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        PlaytimeConfig.getInstance().init();
        PlaytimeDatabase.getInstance().register(Firefly.getInstance().getDatabase());
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Playtime Commands");
        PlaytimeCommand.getCommand().register(Firefly.getInstance());
        new PlaytimeRequirement().register();
        startScheduler();
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        stopScheduler();
        PlaytimeConfig.getInstance().reload();
        startScheduler();
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        stopScheduler();
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider -> {
            provider.addAudiencePlaceholder("playtime", audience -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                return Component.text(getTimeFormatted(player));
            });
            provider.addAudiencePlaceholder("playtime_raw", audience -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                return Component.text(getTime(player));
            });
        });
    }

    // Playtime Management

    private void startScheduler() {
        if (playtimeTask == null) {
            playtimeTask = Bukkit.getScheduler().runTaskTimer(Firefly.getInstance(), () ->
                    Bukkit.getOnlinePlayers().forEach(this::incrementTime), 20L, 20L
            );
        }
    }

    private void stopScheduler() {
        if (playtimeTask != null) {
            playtimeTask.cancel();
            playtimeTask = null;
        }
    }

    public void incrementTime(@NotNull OfflinePlayer player) {
        setTime(player, getTime(player) + 1);
    }

    public void decrementTime(@NotNull OfflinePlayer player) {
        long currentTime = getTime(player);
        if (currentTime > 0) {
            setTime(player, currentTime - 1);
        }
    }

    public void setTime(@NotNull OfflinePlayer player, long time) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setPlaytime(time);
    }

    public long getTime(@NotNull OfflinePlayer player) {
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return 0L;
        }
        return data.getPlaytime();
    }

    public String getTimeFormatted(@NotNull OfflinePlayer player) {
        return DurationFormatter.formatSeconds(getTime(player));
    }

    // Database

    public CompletableFuture<TreeMap<UUID, Long>> getTopPlaytimes() {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = Firefly.getInstance().getDatabase().getConnection().prepareStatement("SELECT * FROM firefly_players")) {
                TreeMap<UUID, Long> top = new TreeMap<>();
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    long value = resultSet.getLong("playtime");
                    top.put(uuid, value);
                }
                resultSet.close();
                return top;
            } catch (SQLException exception) {
                Loggers.error(Firefly.getInstance().getComponentLogger(), "Failed to fetch top playtime data", exception);
                return new TreeMap<>();
            }
        });
    }

}
