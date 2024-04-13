package uk.firedev.skylight.modules.small;

import org.bukkit.plugin.PluginManager;
import uk.firedev.daisylib.Loggers;
import uk.firedev.skylight.config.ModuleConfig;
import uk.firedev.skylight.Skylight;

import java.util.logging.Level;

/**
 * Manager for all small features.
 * Small features are features that only require a single class file.
 */
public class SmallManager {

    private static SmallManager instance = null;

    private SmallManager() {}

    public static SmallManager getInstance() {
        if (instance == null) {
            instance = new SmallManager();
        }
        return instance;
    }

    public void reload() {}

    public void load() {
        PluginManager pluginManager = Skylight.getInstance().getServer().getPluginManager();
        if (ModuleConfig.getInstance().amethystProtectionModuleEnabled()) {
            AmethystProtection.getInstance().setLoaded();
            AmethystProtection.getInstance().register();
            pluginManager.registerEvents(AmethystProtection.getInstance(), Skylight.getInstance());
            Loggers.log(Level.INFO, Skylight.getInstance().getLogger(), "Loaded Amethyst Protection Module.");
        }
        if (ModuleConfig.getInstance().lootChestProtectionModuleEnabled()) {
            pluginManager.registerEvents(LootChestProtection.getInstance(), Skylight.getInstance());
            Loggers.log(Level.INFO, Skylight.getInstance().getLogger(), "Loaded Loot Chest Protection Module.");
        }
    }

}
