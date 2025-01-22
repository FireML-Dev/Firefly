package uk.firedev.firefly;

import org.bukkit.plugin.java.JavaPlugin;
import uk.firedev.firefly.config.GUIConfig;
import uk.firedev.firefly.config.MainConfig;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.modules.ModuleManager;
import uk.firedev.firefly.placeholders.Placeholders;
import uk.firedev.firefly.utils.CommandUtils;

public final class Firefly extends JavaPlugin {

    private static Firefly instance;

    @Override
    public void onEnable() {
        instance = this;

        // Load configs
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        GUIConfig.getInstance().reload();

        Database.getInstance().load();
        FireflyCommand.getInstance().register(this);

        // Handle module loading and their placeholders
        Placeholders.init();
        ModuleManager.getInstance().load();
        Placeholders.register();
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

}
