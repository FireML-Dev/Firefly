package uk.firedev.firefly.modules.command;

import uk.firedev.firefly.Module;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.command.commands.GodmodeCommand;
import uk.firedev.firefly.modules.command.commands.HealCommand;
import uk.firedev.firefly.modules.command.commands.ItemFrameCommand;
import uk.firedev.firefly.modules.command.commands.RenameCommand;
import uk.firedev.firefly.modules.command.commands.RideCommand;
import uk.firedev.firefly.modules.command.commands.flight.FlyCommand;
import uk.firedev.firefly.modules.command.commands.flight.FlySpeedCommand;
import uk.firedev.firefly.modules.command.commands.workstations.AnvilCommand;
import uk.firedev.firefly.modules.command.commands.workstations.CartographyCommand;
import uk.firedev.firefly.modules.command.commands.workstations.GrindstoneCommand;
import uk.firedev.firefly.modules.command.commands.workstations.LoomCommand;
import uk.firedev.firefly.modules.command.commands.workstations.StonecutterCommand;
import uk.firedev.firefly.modules.command.commands.workstations.WorkbenchCommand;

import java.util.List;

public class CommandModule implements Module {

    private static CommandModule instance;

    private boolean loaded = false;
    private final List<SubModule> commands = List.of(
        // Flight
        new FlyCommand(),
        new FlySpeedCommand(),

        // Workstations
        new AnvilCommand(),
        new CartographyCommand(),
        new GrindstoneCommand(),
        new LoomCommand(),
        new StonecutterCommand(),
        new WorkbenchCommand(),

        new RideCommand(),
        new ItemFrameCommand(),
        new GodmodeCommand(),
        new HealCommand(),
        new RenameCommand()
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
        return ModuleConfig.getInstance().isModuleEnabled("commands");
    }

    @Override
    public void init() {
        if (isLoaded()) {
            return;
        }
        CommandConfig.getInstance().init();
        commands.forEach(SubModule::load);
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        CommandConfig.getInstance().reload();
        commands.forEach(SubModule::reload);
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        commands.forEach(SubModule::unload);
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

}
