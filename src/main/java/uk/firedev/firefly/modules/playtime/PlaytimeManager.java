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
import java.util.concurrent.ConcurrentHashMap;

public class PlaytimeManager implements Manager {

    private static PlaytimeManager instance;

    private boolean loaded = false;
    private Map<UUID, Long> playtimeMap = new ConcurrentHashMap<>();
    private MyScheduledTask playtimeTask = null;

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
        PlaytimeDatabase.getInstance().register();
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
        populatePlaytimeMap();
        startScheduler();
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        stopScheduler();
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

    private void startScheduler() {
        if (playtimeTask == null) {
            playtimeTask = Firefly.getScheduler().runTaskTimer(() ->
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
        playtimeMap = new HashMap<>(PlaytimeDatabase.getInstance().getPlaytimes());
    }

    public @NotNull Map<UUID, Long> getPlaytimeMap() {
        if (playtimeMap == null) {
            playtimeMap = new HashMap<>();
        }
        return Map.copyOf(playtimeMap);
    }

    public void saveAllPlaytimes() {
        getPlaytimeMap().forEach(PlaytimeDatabase.getInstance()::saveToDatabase);
    }

}
