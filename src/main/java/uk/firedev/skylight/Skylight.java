package uk.firedev.skylight;

import org.bukkit.plugin.java.JavaPlugin;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.UniversalScheduler;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import uk.firedev.skylight.chat.titles.TitleManager;
import uk.firedev.skylight.config.GUIConfig;
import uk.firedev.skylight.config.MainConfig;
import uk.firedev.skylight.config.MessageConfig;
import uk.firedev.skylight.config.ModuleConfig;
import uk.firedev.skylight.elevator.ElevatorManager;
import uk.firedev.skylight.small.SmallManager;

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
        ModuleConfig.getInstance().reload();
        GUIConfig.getInstance().reload();

        // Load Vault - Stop loading if VaultManager has an issue
        if (!VaultManager.getInstance().reload()) {
            return;
        }

        new SkylightCommand().registerCommand("skylight", this);

        SmallManager.getInstance().load();

        // Load Placeholder Hook
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderReceiver().register();
            Loggers.log(Level.INFO, getLogger(), "Loaded PlaceholderAPI Hook.");
        }

        // Load modules
        if (ModuleConfig.getInstance().elevatorModuleEnabled()) {
            ElevatorManager.getInstance().load();
            Loggers.log(Level.INFO, getLogger(), "Loaded Elevator Module.");
        }
        if (ModuleConfig.getInstance().titleModuleEnabled()) {
            TitleManager.getInstance().load();
            Loggers.log(Level.INFO, getLogger(), "Loaded Titles Module.");
        }

    }

    @Override
    public void onDisable() {}

    public void reload() {
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        ModuleConfig.getInstance().reload();
        GUIConfig.getInstance().reload();
        VaultManager.getInstance().reload();
        ElevatorManager.getInstance().reload();
        TitleManager.getInstance().reload();
        SmallManager.getInstance().reload();
    }

    public static Skylight getInstance() { return instance; }

    public static TaskScheduler getScheduler() { return scheduler; }

    public boolean isPluginEnabled(String plugin) {
        return getServer().getPluginManager().isPluginEnabled(plugin);
    }

}
