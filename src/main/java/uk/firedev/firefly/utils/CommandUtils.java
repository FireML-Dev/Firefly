package uk.firedev.firefly.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.firefly.Firefly;

import java.util.ArrayList;
import java.util.List;

public class CommandUtils {

    private static boolean disabling = false;
    private static BukkitTask cleanupTask = null;
    private static final List<String> commandsToUnregister = new ArrayList<>();

    public static void disable() {
        disabling = true;
        stopTask();
    }

    public static void stopTask() {
        if (cleanupTask == null) {
            return;
        }
        cleanupTask.cancel();
        cleanupTask = null;
    }

    private static void startTask() {
        if (cleanupTask != null || disabling) {
            return;
        }
        cleanupTask = Bukkit.getScheduler().runTaskTimer(Firefly.getInstance(), CommandUtils::slowUnregister, 20L, 20L);
    }

    public static void cancelUnregister(@NotNull String name) {
        commandsToUnregister.remove(name);
        commandsToUnregister.remove("firefly:" + name);
    }

    public static void unregisterCommand(@NotNull String name) {
        CommandAPI.unregister(name);
        CommandAPI.unregister("firefly:" + name);
        //commandsToUnregister.add(name);
        //commandsToUnregister.add("firefly:" + name);
        //startTask();
    }

    private static void slowUnregister() {
        if (commandsToUnregister.isEmpty()) {
            stopTask();
            return;
        }
        System.out.println("Unregistered " + commandsToUnregister.getFirst());
        CommandAPI.unregister(commandsToUnregister.getFirst());
        commandsToUnregister.removeFirst();
    }

}
