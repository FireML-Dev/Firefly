package uk.firedev.firefly;

import uk.firedev.firefly.modules.ModuleManager;

public interface Module {

    String getIdentifier();

    void load();

    void reload();

    void unload();

    boolean isLoaded();

    default boolean register() {
        return ModuleManager.getInstance().registerModule(this);
    }

    default void unregister() {
        ModuleManager.getInstance().unregisterModule(getIdentifier());
    }

}
