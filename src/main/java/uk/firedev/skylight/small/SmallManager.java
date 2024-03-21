package uk.firedev.skylight.small;

import org.bukkit.plugin.PluginManager;
import uk.firedev.daisylib.Loggers;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.config.ModuleConfig;

import java.util.logging.Level;

/**
 * Manager for all small features.
 * Small features are features that only require a single class file.
 */
public class SmallManager {

    private static SmallManager instance = null;

    private final Skylight plugin;

    private SmallManager() {
        plugin = Skylight.getInstance();
    }

    public static SmallManager getInstance() {
        if (instance == null) {
            instance = new SmallManager();
        }
        return instance;
    }

    public void reload() {
    }

    public void load() {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        if (ModuleConfig.getInstance().amethystProtectionModuleEnabled()) {
            AmethystProtection.getInstance().setLoaded();
            AmethystProtection.getInstance().registerCommand("amethystprotect", plugin);
            pluginManager.registerEvents(AmethystProtection.getInstance(), plugin);
            Loggers.log(Level.INFO, plugin.getLogger(), "Loaded Amethyst Protection Module.");
        }
        if (ModuleConfig.getInstance().lootChestProtectionModuleEnabled()) {
            pluginManager.registerEvents(LootChestProtection.getInstance(), plugin);
            Loggers.log(Level.INFO, plugin.getLogger(), "Loaded Loot Chest Protection Module.");
        }
    }

}
