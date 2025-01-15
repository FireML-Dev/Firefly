package uk.firedev.firefly.modules.command;

import uk.firedev.firefly.Module;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.ModuleManager;
import uk.firedev.firefly.modules.command.commands.flight.FlyCommand;
import uk.firedev.firefly.modules.command.commands.flight.FlySpeedCommand;
import uk.firedev.firefly.modules.command.commands.RideCommand;

import java.util.List;

public class CommandModule implements Module {

    private static CommandModule instance;

    private boolean loaded = false;
    private final List<SubModule> commands = List.of(
        new FlyCommand(),
        new FlySpeedCommand(),
        new RideCommand()
    );

    private CommandModule() {}

    public static CommandModule getInstance() {
        if (instance == null) {
            instance = new CommandModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "Commands";
    }

    @Override
    public boolean isConfigEnabled() {
        return ModuleConfig.getInstance().commandsModuleEnabled();
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        loaded = true;
        reload();
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        CommandConfig.getInstance().reload();
        // TODO look into why commands aren't being unregistered.
        commands.forEach(command -> ModuleManager.getInstance().registerOrUnregisterModule(command));
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        commands.forEach(SubModule::unregister);
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

}
