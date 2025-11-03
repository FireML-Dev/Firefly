package uk.firedev.firefly.modules.command;

import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;
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
        CommandConfig.getInstance().init();
        commands.forEach(SubModule::load);
    }

    @Override
    public void registerCommands(@NotNull Commands registrar) {}

    @Override
    public void reload() {
        CommandConfig.getInstance().reload();
        commands.forEach(SubModule::reload);
    }

    @Override
    public void unload() {
        commands.forEach(SubModule::unload);
    }

}
