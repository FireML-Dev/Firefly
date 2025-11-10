package uk.firedev.firefly;

import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.firefly.config.MessageConfig;

public interface SubModule {

    boolean isConfigEnabled();

    void init();

    default void load() {
        if (!isConfigEnabled()) {
            return;
        }
        init();
        if (this instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, Firefly.getInstance());
        }
        if (this instanceof CommandHolder commandHolder) {
            commandHolder.initCommand();
        }
        registerPlaceholders();
    }

    void reload();

    void unload();

    default void registerPlaceholders() {}

    /**
     * Checks if the submodule is enabled and sends a message if not.
     * @param sender The relevant sender
     */
    default boolean checkEnabled(@Nullable CommandSender sender) {
        if (!isConfigEnabled()) {
            MessageConfig.getInstance().getFeatureDisabledMessage().send(sender);
            return false;
        }
        return true;
    }

}
