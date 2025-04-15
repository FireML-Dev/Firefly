package uk.firedev.firefly;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import uk.firedev.firefly.config.MessageConfig;

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
