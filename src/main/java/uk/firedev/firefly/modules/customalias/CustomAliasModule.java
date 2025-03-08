package uk.firedev.firefly.modules.customalias;

import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.utils.CommandUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomAliasModule implements Module {

    private static CustomAliasModule instance;

    private boolean loaded = false;
    private final List<String> loadedCommands;

    private CustomAliasModule() {
        loadedCommands = new ArrayList<>();
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
        return ModuleConfig.getInstance().isModuleEnabled("aliases");
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        CustomAliasConfig.getInstance().init();
        loadAllCommands();
        loaded = true;
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
        loaded = false;
        loadedCommands.clear();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    private void loadAllCommands() {
        List<String> configCommands = CustomAliasConfig.getInstance().getCommandBuilderNames();
        Iterator<String> loadedCommandsIterator = loadedCommands.iterator();
        while (loadedCommandsIterator.hasNext()) {
            String command = loadedCommandsIterator.next();
            if (!configCommands.contains(command)) {
                CommandUtils.unregisterCommand(command);
                loadedCommandsIterator.remove();
            }
        }
        CustomAliasConfig.getInstance().getCommandBuilders().forEach(builder -> {

            String name = builder.getCommandName();
            if (name == null || name.isEmpty()) {
                return;
            }

            // Handle list things
            if (!loadedCommands.contains(name)) {
                loadedCommands.add(name);
            }

            for (String alias : builder.getAliases()) {
                // Remove from unregister list if it is present
                if (!loadedCommands.contains(alias)) {
                    loadedCommands.add(alias);
                }
            }

            // Command registration
            builder.registerCommand();
        });
    }

}
