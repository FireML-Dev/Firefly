package uk.firedev.firefly;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public interface SubModule extends Listener {

    boolean isConfigEnabled();

    void load();

    void reload();

    void unload();

    boolean isLoaded();

    default boolean register() {
        load();
        Bukkit.getPluginManager().registerEvents(this, Firefly.getInstance());
        registerPlaceholders();
        return true;
    }

    default void unregister() {
        unload();
        HandlerList.unregisterAll(this);
    }

    default void registerPlaceholders() {}

}
