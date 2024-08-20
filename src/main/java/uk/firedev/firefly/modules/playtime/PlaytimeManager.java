package uk.firedev.firefly.modules.playtime;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.daisylib.requirement.RequirementManager;
import uk.firedev.daisylib.utils.DurationFormatter;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Manager;
import uk.firedev.firefly.modules.playtime.command.PlaytimeCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaytimeManager implements Manager {

    private static PlaytimeManager instance;

    private boolean loaded = false;
    private Map<UUID, Long> playtimeMap = new HashMap<>();
    private MyScheduledTask playtimeTask = null;
    private MyScheduledTask databaseTask = null;

    private PlaytimeManager() {}

    public static PlaytimeManager getInstance() {
        if (instance == null) {
            instance = new PlaytimeManager();
        }
        return instance;
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        PlaytimeConfig.getInstance().reload();
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Playtime Commands");
        PlaytimeCommand.getInstance().register(Firefly.getInstance());
        new PlaytimeRequirement().register();
        populatePlaytimeMap();
        startSchedulers();
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        stopSchedulers();
        PlaytimeConfig.getInstance().reload();
        populatePlaytimeMap();
        startSchedulers();
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        stopSchedulers();
        RequirementManager.getInstance().unregisterRequirement("playtime");
        // Unregister Commands
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Unregistering Playtime Commands");
        CommandAPI.unregister("playtime");
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    // Playtime Management

    private void startSchedulers() {
        if (playtimeTask == null) {
            playtimeTask = Firefly.getScheduler().runTaskTimer(() ->
                    Bukkit.getOnlinePlayers().forEach(this::incrementTime), 20L, 20L
            );
        }
        if (databaseTask == null) {
            // seconds * 20 = ticks
            long saveInterval = PlaytimeConfig.getInstance().getSaveInterval() * 20;
            databaseTask = Firefly.getScheduler().runTaskTimer(this::saveAllPlaytimes, saveInterval, saveInterval);
        }
    }

    private void stopSchedulers() {
        if (playtimeTask != null) {
            playtimeTask.cancel();
            playtimeTask = null;
        }
        if (databaseTask != null) {
            databaseTask.cancel();
            databaseTask = null;
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
        playtimeMap = new HashMap<>(PlaytimeDatabase.getInstance().getPlaytimes());
    }

    public @NotNull Map<UUID, Long> getPlaytimeMap() {
        if (playtimeMap == null) {
            playtimeMap = new HashMap<>();
        }
        return Map.copyOf(playtimeMap);
    }

    public void saveAllPlaytimes() {
        getPlaytimeMap().forEach(PlaytimeDatabase.getInstance()::setPlaytime);
    }

}
