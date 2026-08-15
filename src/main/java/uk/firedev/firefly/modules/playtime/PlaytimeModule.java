package uk.firedev.firefly.modules.playtime;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.utils.DurationFormatter;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.playtime.command.PlaytimeCommand;
import uk.firedev.firefly.modules.playtime.placeholders.PlaytimePlaceholder;
import uk.firedev.firefly.modules.playtime.placeholders.PlaytimeRawPlaceholder;
import uk.firedev.firefly.placeholders.FireflyPlaceholder;
import uk.firedev.firefly.placeholders.FireflyPlaceholders;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PlaytimeModule implements Module {

    private static PlaytimeModule instance;

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
    public void init() {
        PlaytimeDatabase.getInstance().register(Firefly.getInstance().getDatabase());
        new PlaytimeRequirement().register();
        startScheduler();
        new PlaytimeCommand().initCommand();
        registerPlaceholders();
    }

    @Override
    public void reload() {
        stopScheduler();
        PlaytimeConfig.getInstance().reload();
        startScheduler();
    }

    @Override
    public void unload() {
        stopScheduler();
    }

    private void registerPlaceholders() {
        FireflyPlaceholders.get().add(new PlaytimePlaceholder(this));
        FireflyPlaceholders.get().add(new PlaytimeRawPlaceholder(this));
    }

    // Playtime Management

    private void startScheduler() {
        if (!isConfigEnabled()) {
            return;
        }
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

    public void incrementTime(@NonNull OfflinePlayer player) {
        if (!isConfigEnabled()) {
            return;
        }
        setTime(player, getTime(player) + 1);
    }

    public void decrementTime(@NonNull OfflinePlayer player) {
        if (!isConfigEnabled()) {
            return;
        }
        long currentTime = getTime(player);
        if (currentTime > 0) {
            setTime(player, currentTime - 1);
        }
    }

    public void setTime(@NonNull OfflinePlayer player, long time) {
        if (!isConfigEnabled()) {
            return;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        data.setPlaytime(time);
    }

    public long getTime(@NonNull OfflinePlayer player) {
        if (!isConfigEnabled()) {
            return 0L;
        }
        PlayerData data = Firefly.getInstance().getDatabase().getPlayerData(player.getUniqueId());
        if (data == null) {
            return 0L;
        }
        return data.getPlaytime();
    }

    public String getTimeFormatted(@NonNull OfflinePlayer player) {
        return new DurationFormatter(TimeUnit.SECONDS).format(getTime(player));
    }

    // Database

    public CompletableFuture<Map<Long, UUID>> getTopPlaytimes() {
        if (!isConfigEnabled()) {
            return CompletableFuture.completedFuture(Map.of());
        }
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = Firefly.getInstance().getDatabase().getConnection().prepareStatement("SELECT * FROM firefly_players")) {
                Map<Long, UUID> top = new TreeMap<>(Collections.reverseOrder());
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    long value = resultSet.getLong("playtime");
                    top.put(value, uuid);
                }
                resultSet.close();
                return top;
            } catch (SQLException exception) {
                Firefly.getInstance().getLogging().error("Failed to fetch top playtime data", exception);
                return new TreeMap<>();
            }
        });
    }

}
