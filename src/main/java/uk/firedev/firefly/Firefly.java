package uk.firedev.firefly;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.java.JavaPlugin;
import uk.firedev.firefly.config.GUIConfig;
import uk.firedev.firefly.config.MainConfig;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.modules.ModuleManager;
import uk.firedev.firefly.placeholders.Placeholders;

public final class Firefly extends JavaPlugin {

    private static Firefly instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Load configs
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        GUIConfig.getInstance().reload();

        Database.getInstance().load();
        FireflyCommand.getCommand().register(this);

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
        instance = null;
    }

    public void reload() {
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        GUIConfig.getInstance().reload();
        ModuleManager.getInstance().reload();
        Database.getInstance().reload();
    }

    public static Firefly getInstance() {
        Preconditions.checkArgument(instance != null, "Firefly is not enabled!");
        return instance;
    }

}
