package uk.firedev.firefly;

import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public interface SubModule {

    boolean isConfigEnabled();

    void init();

    void registerCommands(@NotNull Commands registrar);

    default void load() {
        if (!isConfigEnabled()) {
            return;
        }
        init();
        if (this instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, Firefly.getInstance());
        }
        registerPlaceholders();
    }

    void reload();

    void unload();

    default void registerPlaceholders() {}

}
