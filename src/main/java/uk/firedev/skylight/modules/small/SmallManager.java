package uk.firedev.skylight.modules.small;

import org.bukkit.plugin.PluginManager;
import uk.firedev.daisylib.Loggers;
import uk.firedev.skylight.Manager;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.config.ModuleConfig;

import java.util.logging.Level;

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
        PluginManager pluginManager = Skylight.getInstance().getServer().getPluginManager();
        if (ModuleConfig.getInstance().amethystProtectionModuleEnabled()) {
            AmethystProtection.getInstance().load();
            pluginManager.registerEvents(AmethystProtection.getInstance(), Skylight.getInstance());
            Loggers.info(Skylight.getInstance().getComponentLogger(), "Loaded Amethyst Protection Module.");
        }
        if (ModuleConfig.getInstance().lootChestProtectionModuleEnabled()) {
            LootChestProtection.getInstance().load();
            Loggers.info(Skylight.getInstance().getComponentLogger(), "Loaded Loot Chest Protection Module.");
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
