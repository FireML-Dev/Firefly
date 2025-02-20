package uk.firedev.firefly.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.firefly.Firefly;

import java.util.ArrayList;
import java.util.List;

public class CommandUtils {

    private static BukkitTask task = null;
    private static final List<String> commandsToRemove = new ArrayList<>();

    public static void unregisterCommand(@NotNull String name) {
        commandsToRemove.add(name);
        commandsToRemove.add("firefly:" + name);
        if (task == null || task.isCancelled()) {
            task = Bukkit.getScheduler().runTaskTimer(Firefly.getInstance(), () -> {
                if (commandsToRemove.isEmpty()) {
                    stopTask();
                    return;
                }
                String command = commandsToRemove.removeFirst();
                CommandAPI.unregister(command);
            }, 20L, 20L);
        }
    }

    public static void stopTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

}
