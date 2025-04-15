package uk.firedev.firefly.modules.command;

import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.commandsenders.BukkitCommandSender;
import uk.firedev.daisylib.libs.commandapi.executors.ExecutionInfo;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.config.MessageConfig;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class Command implements SubModule {

    private boolean loaded;
    private final CommandTree command;

    protected Command() {
        this.command = loadCommand();
    }

    public abstract @NotNull CommandTree loadCommand();

    public @NotNull CommandTree getCommand() {
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

    protected boolean disabledCheck(@NotNull Audience audience) {
        if (!isLoaded()) {
            MessageConfig.getInstance().getFeatureDisabledMessage().sendMessage(audience);
            return true;
        }
        return false;
    }

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
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public @NotNull String getPermission() {
        return CommandConfig.getInstance().getConfig().getString(
                getConfigName() + ".permission",
                "firefly.command." + getConfigName().toLowerCase()
        );
    }

    public @NotNull String getTargetPermission() {
        return CommandConfig.getInstance().getConfig().getString(
                getConfigName() + ".permission-target",
                "firefly.command." + getConfigName().toLowerCase() + ".other"
        );
    }

}
