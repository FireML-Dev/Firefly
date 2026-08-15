package uk.firedev.firefly;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.DaisyLib;
import uk.firedev.daisylib.database.exceptions.DatabaseLoadException;
import uk.firedev.daisylib.logging.ComponentLogging;
import uk.firedev.daisylib.logging.Logging;
import uk.firedev.firefly.config.MainConfig;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.modules.ModuleManager;
import uk.firedev.firefly.placeholders.FireflyPlaceholders;
import uk.firedev.firefly.utils.ChatListener;

public final class Firefly extends JavaPlugin {

    private static Firefly INSTANCE;

    private final Database database = new Database(this);
    private final ComponentLogging logging = Logging.logging(this);

    public Firefly() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException(getClass().getName() + " has already been assigned!");
        }
        INSTANCE = this;
    }

    public static @NonNull Firefly getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(Firefly.class.getSimpleName() + " has not been assigned!");
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        DaisyLib.get().init(this);
        loadDatabase();
        registerCommands();
        registerListeners();
        ModuleManager.getInstance().load();
        FireflyPlaceholders.get().register();
    }

    @Override
    public void onDisable() {
        ModuleManager.getInstance().unload();
        // DO THIS LAST!!!!
        database.unload();
    }

    public void reload() {
        MainConfig.getInstance().reload();
        MessageConfig.getInstance().reload();
        ModuleManager.getInstance().reload();
        database.reload();
    }

    public @NonNull Database getDatabase() {
        return this.database;
    }

    public @NonNull ComponentLogging getLogging() {
        return this.logging;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }

    private void loadDatabase() {
        try {
            database.load();
        } catch (DatabaseLoadException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(FireflyCommand.get());
        });
    }

}
