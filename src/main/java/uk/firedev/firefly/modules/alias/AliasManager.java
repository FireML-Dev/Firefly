package uk.firedev.firefly.modules.alias;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AliasManager implements Manager {

    private static AliasManager instance;

    private boolean loaded = false;
    private List<String> loadedCommands;
    private List<String> commandsToUnregister;
    private MyScheduledTask cleanupTask;

    private AliasManager() {
        loadedCommands = new ArrayList<>();
        commandsToUnregister = new ArrayList<>();
    }

    public static AliasManager getInstance() {
        if (instance == null) {
            instance = new AliasManager();
        }
        return instance;
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
        AliasConfig.getInstance().reload();
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
        cleanupTask = Firefly.getScheduler().runTaskTimer(this::slowUnregister, 20L, 20L);
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
            System.out.println("Disabled task because nothing to unregister");
            return;
        }
        System.out.println("Unregistering " + commandsToUnregister.getFirst());
        CommandAPI.unregister(commandsToUnregister.getFirst());
        commandsToUnregister.removeFirst();
    }

    private void addToUnregisterList(@NotNull String command) {
        commandsToUnregister.add(command);
        startTask();
    }

    private void loadAllCommands() {
        List<String> configCommands = AliasConfig.getInstance().getCommandBuilderNames();
        Iterator<String> loadedCommandsIterator = loadedCommands.iterator();
        while (loadedCommandsIterator.hasNext()) {
            String command = loadedCommandsIterator.next();
            if (!configCommands.contains(command)) {
                // Add to list via method so we can start the removal task if needed
                addToUnregisterList(command);
                loadedCommandsIterator.remove();
            }
        }
        AliasConfig.getInstance().getCommandBuilders().forEach(builder -> {
            String name = builder.getCommandName();
            if (name == null || name.isEmpty()) {
                return;
            }
            // Remove from unregister list if it is present
            commandsToUnregister.remove(name);
            // Command registration
            builder.registerCommand();
            loadedCommands.add(name);
            loadedCommands.addAll(builder.getAliases());
        });
    }

}
