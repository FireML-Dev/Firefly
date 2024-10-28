package uk.firedev.firefly;

import org.bukkit.plugin.java.JavaPlugin;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.UniversalScheduler;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import uk.firedev.firefly.config.GUIConfig;
import uk.firedev.firefly.config.MainConfig;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.modules.ModuleManager;
import uk.firedev.firefly.placeholders.MiniPlaceholdersExpansion;
import uk.firedev.firefly.placeholders.PlaceholderAPIExpansion;

public final class Firefly extends JavaPlugin {

    private static Firefly instance;
    private static TaskScheduler scheduler;

    @Override
    public void onEnable() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);

        // Load configs
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        GUIConfig.getInstance().reload();

        Database.getInstance().load();
        FireflyCommand.getInstance().register(this);

        // Load Placeholder Hooks
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIExpansion().register();
            Loggers.info(getComponentLogger(), "Loaded PlaceholderAPI Hook.");
        }
        if (getServer().getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            new MiniPlaceholdersExpansion().register();
            Loggers.info(getComponentLogger(), "Loaded MiniPlaceholders Hook.");
        }
        ModuleManager.getInstance().load();
    }

    @Override
    public void onDisable() {
        ModuleManager.getInstance().unload();
        // DO THIS LAST!!!!
        Database.getInstance().unload();
    }

    public void reload() {
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        GUIConfig.getInstance().reload();
        ModuleManager.getInstance().reload();
        Database.getInstance().reload();
    }

    public static Firefly getInstance() { return instance; }

    public static TaskScheduler getScheduler() { return scheduler; }

}
