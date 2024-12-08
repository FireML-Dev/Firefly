package uk.firedev.firefly.modules.customcommands;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomCommandsModule implements Module {

    private static CustomCommandsModule instance;

    private boolean loaded = false;
    private List<String> loadedCommands;
    private List<String> commandsToUnregister;
    private BukkitTask cleanupTask;

    private CustomCommandsModule() {
        loadedCommands = new ArrayList<>();
        commandsToUnregister = new ArrayList<>();
    }

    public static CustomCommandsModule getInstance() {
        if (instance == null) {
            instance = new CustomCommandsModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "CustomCommands";
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        loaded = true;
        loadAllCommands();
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        CustomCommandsConfig.getInstance().reload();
        loadAllCommands();
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        stopTask();
        loaded = false;
        loadedCommands.clear();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    private void startTask() {
        if (cleanupTask != null) {
            return;
        }
        cleanupTask = Bukkit.getScheduler().runTaskTimer(Firefly.getInstance(), this::slowUnregister, 20L, 20L);
    }

    private void stopTask() {
        if (cleanupTask == null) {
            return;
        }
        cleanupTask.cancel();
        cleanupTask = null;
    }

    private void slowUnregister() {
        if (commandsToUnregister.isEmpty()) {
            stopTask();
            return;
        }
        CommandAPI.unregister(commandsToUnregister.getFirst());
        commandsToUnregister.removeFirst();
    }

    private void addToUnregisterList(@NotNull String command) {
        commandsToUnregister.add(command);
        startTask();
    }

    private void loadAllCommands() {
        List<String> configCommands = CustomCommandsConfig.getInstance().getCommandBuilderNames();
        Iterator<String> loadedCommandsIterator = loadedCommands.iterator();
        while (loadedCommandsIterator.hasNext()) {
            String command = loadedCommandsIterator.next();
            if (!configCommands.contains(command)) {
                // Add to list via method so we can start the removal task if needed
                addToUnregisterList(command);
                loadedCommandsIterator.remove();
            }
        }
        CustomCommandsConfig.getInstance().getCommandBuilders().forEach(builder -> {
            String name = builder.getCommandName();
            if (name == null || name.isEmpty()) {
                return;
            }

            // Handle list things
            commandsToUnregister.remove(name);
            if (!loadedCommands.contains(name)) {
                loadedCommands.add(name);
            }

            for (String alias : builder.getAliases()) {
                // Remove from unregister list if it is present
                commandsToUnregister.remove(alias);
                if (!loadedCommands.contains(alias)) {
                    loadedCommands.add(alias);
                }
            }

            // Command registration
            builder.registerCommand();
        });
    }

}
