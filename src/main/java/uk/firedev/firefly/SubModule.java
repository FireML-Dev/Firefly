package uk.firedev.firefly;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

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
        registerPlaceholders();
    }

    void reload();

    void unload();

    default void registerPlaceholders() {}

}
