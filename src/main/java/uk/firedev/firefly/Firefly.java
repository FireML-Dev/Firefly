package uk.firedev.firefly;

import com.google.common.base.Preconditions;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.database.exceptions.DatabaseLoadException;
import uk.firedev.firefly.config.MainConfig;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.modules.ModuleManager;
import uk.firedev.firefly.placeholders.Placeholders;

public final class Firefly extends JavaPlugin {

    private static Firefly instance;

    private final Database database = new Database(this);

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Load configs
        MainConfig.getInstance().init();
        MessageConfig.getInstance().init();

        try {
            database.load();
        } catch (DatabaseLoadException exception) {
            throw new RuntimeException(exception);
        }

        // Register command
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new FireflyCommand().get());
        });

        // Handle module loading and their placeholders
        Placeholders.init();
        ModuleManager.getInstance().load();
        Placeholders.register();
    }

    @Override
    public void onDisable() {
        ModuleManager.getInstance().unload();
        // DO THIS LAST!!!!
        database.unload();
        instance = null;
    }

    public void reload() {
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        ModuleManager.getInstance().reload();
        database.reload();
    }

    public @NotNull Database getDatabase() {
        return this.database;
    }

    public static Firefly getInstance() {
        Preconditions.checkArgument(instance != null, "Firefly is not enabled!");
        return instance;
    }

}
