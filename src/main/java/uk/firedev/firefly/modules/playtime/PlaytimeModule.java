package uk.firedev.firefly.modules.playtime;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.utils.DurationFormatter;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.modules.playtime.command.PlaytimeCommand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaytimeModule implements Module {

    private static PlaytimeModule instance;

    private boolean loaded = false;
    private final Map<UUID, Long> playtimeMap;
    private BukkitTask playtimeTask = null;

    private PlaytimeModule() {
        playtimeMap = new ConcurrentHashMap<>();
    }

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
    public void load() {
        if (isLoaded()) {
            return;
        }
        PlaytimeConfig.getInstance().reload();
        PlaytimeDatabase.getInstance().register(Database.getInstance());
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Playtime Commands");
        PlaytimeCommand.getCommand().register(Firefly.getInstance());
        new PlaytimeRequirement().register();
        populatePlaytimeMap();
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
        // Save all times so we don't lose them
        saveAllPlaytimes();
    }

    public void incrementTime(@NotNull OfflinePlayer player) {
        playtimeMap.put(player.getUniqueId(), (getTime(player) + 1));
    }

    public void decrementTime(@NotNull OfflinePlayer player) {
        long currentTime = getTime(player);
        if (currentTime > 0) {
            playtimeMap.put(player.getUniqueId(), currentTime - 1);
        }
    }

    public void setTime(@NotNull OfflinePlayer player, long time) {
        playtimeMap.put(player.getUniqueId(), time);
    }

    public long getTime(@NotNull OfflinePlayer player) {
        return playtimeMap.getOrDefault(player.getUniqueId(), 0L);
    }

    public String getTimeFormatted(@NotNull OfflinePlayer player) {
        return DurationFormatter.formatSeconds(getTime(player));
    }

    public void populatePlaytimeMap() {
        synchronized (playtimeMap) {
            playtimeMap.clear();
            playtimeMap.putAll(PlaytimeDatabase.getInstance().getPlaytimes());
        }
    }

    public @NotNull Map<UUID, Long> getPlaytimeMap() {
        return Map.copyOf(playtimeMap);
    }

    public void saveAllPlaytimes() {
        getPlaytimeMap().forEach(PlaytimeDatabase.getInstance()::saveToDatabase);
    }

}
