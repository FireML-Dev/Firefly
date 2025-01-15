package uk.firedev.firefly.modules.customalias;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.ModuleConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomAliasModule implements Module {

    private static CustomAliasModule instance;

    private boolean loaded = false;
    private final List<String> loadedCommands;
    private final List<String> commandsToUnregister;
    private BukkitTask cleanupTask;

    private CustomAliasModule() {
        loadedCommands = new ArrayList<>();
        commandsToUnregister = new ArrayList<>();
    }

    public static CustomAliasModule getInstance() {
        if (instance == null) {
            instance = new CustomAliasModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "CustomAliases";
    }

    @Override
    public boolean isConfigEnabled() {
        return ModuleConfig.getInstance().aliasesModuleEnabled();
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
        CustomAliasConfig.getInstance().reload();
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
        commandsToUnregister.add("firefly:" + command);
        startTask();
    }

    private void loadAllCommands() {
        List<String> configCommands = CustomAliasConfig.getInstance().getCommandBuilderNames();
        Iterator<String> loadedCommandsIterator = loadedCommands.iterator();
        while (loadedCommandsIterator.hasNext()) {
            String command = loadedCommandsIterator.next();
            if (!configCommands.contains(command)) {
                // Add to list via method so we can start the removal task if needed
                addToUnregisterList(command);
                loadedCommandsIterator.remove();
            }
        }
        CustomAliasConfig.getInstance().getCommandBuilders().forEach(builder -> {

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
