package uk.firedev.skylight.modules.alias;

import uk.firedev.daisylib.libs.commandapi.CommandAPI;
import uk.firedev.skylight.Manager;

import java.util.ArrayList;
import java.util.List;

public class AliasManager implements Manager {

    private static AliasManager instance;

    private boolean loaded = false;
    private List<String> loadedCommands = new ArrayList<>();

    private AliasManager() {}

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
        loaded = false;
        if (loadedCommands != null) {
            loadedCommands.clear();
        }
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    private void loadAllCommands() {
        if (loadedCommands == null) {
            loadedCommands = new ArrayList<>();
        }
        if (!loadedCommands.isEmpty()) {
            loadedCommands.forEach(CommandAPI::unregister);
            loadedCommands.clear();
        }
        AliasConfig.getInstance().getCommandBuilders().forEach(builder -> {
            String name = builder.getCommandName();
            if (name != null && !loadedCommands.contains(name)) {
                builder.registerCommand();
                loadedCommands.add(name);
            }
        });
    }

}
