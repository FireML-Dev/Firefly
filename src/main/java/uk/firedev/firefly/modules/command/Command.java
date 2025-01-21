package uk.firedev.firefly.modules.command;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.utils.CommandUtils;

public abstract class Command implements SubModule {

    private boolean loaded;

    public abstract String getName();
    public abstract CommandTree getCommand();

    @Override
    public boolean isConfigEnabled() {
        return CommandConfig.getInstance().getConfig().getBoolean(getName() + ".enabled", true);
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        getCommand().register(Firefly.getInstance());
        loaded = true;
    }

    @Override
    public void reload() { /* There is nothing to reload here :) */ }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        CommandUtils.unregisterCommand(getName());
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public @NotNull String getPermission() {
        return "firefly.command." + getName().toLowerCase();
    }

    public @NotNull String getTargetPermission() {
        return getPermission() + ".other";
    }

}
