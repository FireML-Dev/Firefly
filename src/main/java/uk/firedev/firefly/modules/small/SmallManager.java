package uk.firedev.firefly.modules.small;

import org.bukkit.plugin.PluginManager;
import uk.firedev.daisylib.Loggers;
import uk.firedev.firefly.Manager;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.ModuleConfig;

/**
 * Manager for all small features.
 * Small features are features that only require a single class file.
 */
public class SmallManager implements Manager {

    private static SmallManager instance = null;

    private boolean loaded = false;

    private SmallManager() {}

    public static SmallManager getInstance() {
        if (instance == null) {
            instance = new SmallManager();
        }
        return instance;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        SmallConfig.getInstance().reload();
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        SmallConfig.getInstance().reload();
        PluginManager pluginManager = Firefly.getInstance().getServer().getPluginManager();
        if (ModuleConfig.getInstance().amethystProtectionModuleEnabled()) {
            AmethystProtection.getInstance().load();
            pluginManager.registerEvents(AmethystProtection.getInstance(), Firefly.getInstance());
            Loggers.info(Firefly.getInstance().getComponentLogger(), "Loaded Amethyst Protection Module.");
        }
        if (ModuleConfig.getInstance().lootChestProtectionModuleEnabled()) {
            LootChestProtection.getInstance().load();
            Loggers.info(Firefly.getInstance().getComponentLogger(), "Loaded Loot Chest Protection Module.");
        }
        loaded = true;
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        AmethystProtection.getInstance().unload();
        LootChestProtection.getInstance().unload();
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

}
