package uk.firedev.skylight.small;

import org.bukkit.plugin.PluginManager;
import uk.firedev.daisylib.Loggers;
import uk.firedev.skylight.Modules;
import uk.firedev.skylight.Skylight;

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

    public void reload() {}

    public void load() {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        if (Modules.amethystProtectionModuleEnabled()) {
            AmethystProtection.getInstance().setLoaded();
            AmethystProtection.getInstance().register();
            pluginManager.registerEvents(AmethystProtection.getInstance(), plugin);
            Loggers.log(Level.INFO, plugin.getLogger(), "Loaded Amethyst Protection Module.");
        }
        if (Modules.lootChestProtectionModuleEnabled()) {
            pluginManager.registerEvents(LootChestProtection.getInstance(), plugin);
            Loggers.log(Level.INFO, plugin.getLogger(), "Loaded Loot Chest Protection Module.");
        }
    }

}
