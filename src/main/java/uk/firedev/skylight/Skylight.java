package uk.firedev.skylight;

import org.bukkit.plugin.java.JavaPlugin;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.UniversalScheduler;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import uk.firedev.skylight.modules.Modules;
import uk.firedev.skylight.modules.titles.TitleManager;
import uk.firedev.skylight.config.GUIConfig;
import uk.firedev.skylight.config.MainConfig;
import uk.firedev.skylight.config.MessageConfig;
import uk.firedev.skylight.modules.elevator.ElevatorManager;
import uk.firedev.skylight.modules.kit.KitManager;
import uk.firedev.skylight.placeholders.MiniPlaceholdersExpansion;
import uk.firedev.skylight.placeholders.PlaceholderAPIExpansion;
import uk.firedev.skylight.modules.small.SmallManager;

import java.util.logging.Level;

public final class Skylight extends JavaPlugin {

    private static Skylight instance;
    private static TaskScheduler scheduler;

    @Override
    public void onEnable() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);

        // Load configs
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        GUIConfig.getInstance().reload();

        SkylightCommand.getInstance().register();

        // Load Placeholder Hooks
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIExpansion().register();
            Loggers.log(Level.INFO, getLogger(), "Loaded PlaceholderAPI Hook.");
        }
        if (getServer().getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            new MiniPlaceholdersExpansion().register();
            Loggers.log(Level.INFO, getLogger(), "Loaded MiniPlaceholders Hook.");
        }

        // Load modules
        SmallManager.getInstance().load();
        if (Modules.elevatorModuleEnabled()) {
            ElevatorManager.getInstance().load();
            Loggers.log(Level.INFO, getLogger(), "Loaded Elevator Module.");
        }
        if (Modules.titleModuleEnabled()) {
            TitleManager.getInstance().load();
            Loggers.log(Level.INFO, getLogger(), "Loaded Title Module.");
        }
        if (Modules.kitsModuleEnabled()) {
            KitManager.getInstance().load();
            Loggers.log(Level.INFO, getLogger(), "Loaded Kits Module.");
        }

    }

    @Override
    public void onDisable() {}

    public void reload() {
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        GUIConfig.getInstance().reload();
        if (ElevatorManager.getInstance().isLoaded()) {
            ElevatorManager.getInstance().reload();
        }
        if (TitleManager.getInstance().isLoaded()) {
            TitleManager.getInstance().reload();
        }
        if (KitManager.getInstance().isLoaded()) {
            KitManager.getInstance().reload();
        }
        SmallManager.getInstance().reload();
    }

    public static Skylight getInstance() { return instance; }

    public static TaskScheduler getScheduler() { return scheduler; }

    public boolean isPluginEnabled(String plugin) {
        return getServer().getPluginManager().isPluginEnabled(plugin);
    }

}
