package uk.firedev.skylight.small;

import uk.firedev.daisylib.Loggers;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.config.ModuleConfig;

import java.util.logging.Level;

public class SmallManager {

    private static SmallManager instance = null;

    private final Skylight plugin;
    private boolean loaded = false;

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
        if (!loaded) {
            return;
        }
    }

    public void load() {
        if (ModuleConfig.getInstance().amethystProtectionModuleEnabled()) {
            AmethystProtection.getInstance().setLoaded();
            AmethystProtection.getInstance().registerCommand("amethystprotect", plugin);
            plugin.getServer().getPluginManager().registerEvents(AmethystProtection.getInstance(), plugin);
            Loggers.log(Level.INFO, plugin.getLogger(), "Loaded Amethyst Protection Module.");
        }
        loaded = true;
    }

}
