package uk.firedev.firefly.modules.command;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.utils.CommandUtils;

import java.util.Arrays;
import java.util.Objects;

public abstract class Command implements SubModule {

    private boolean loaded;
    private CommandTree command;

    public @NotNull CommandTree getCommand() {
        if (command == null) {
            throw new RuntimeException("Command is not available! This should not be the case!");
        }
        return command;
    }

    @Override
    public boolean isConfigEnabled() {
        return CommandConfig.getInstance().getConfig().getBoolean(getConfigName() + ".enabled", true);
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        command = refreshCommand();
        command.register(Firefly.getInstance());
        loaded = true;
    }

    public abstract @NotNull String getConfigName();

    public @NotNull String getName() {
        return Objects.requireNonNullElse(
                CommandConfig.getInstance().getConfig().getString(getConfigName() + ".name"),
                getConfigName()
        );
    }

    public String[] getAliases() {
        return CommandConfig.getInstance().getConfig().getStringList(getConfigName() + ".aliases").toArray(String[]::new);
    }

    public abstract @NotNull CommandTree refreshCommand();

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        unload();
        load();
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        if (command != null) {
            CommandUtils.unregisterCommand(command.getName());
            Arrays.stream(command.getAliases()).forEach(CommandUtils::unregisterCommand);
        }
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public @NotNull String getPermission() {
        return "firefly.command." + getConfigName().toLowerCase();
    }

    public @NotNull String getTargetPermission() {
        return getPermission() + ".other";
    }

}
