package uk.firedev.firefly;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.UniversalScheduler;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import uk.firedev.firefly.config.GUIConfig;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.database.Database;
import uk.firedev.firefly.modules.alias.AliasManager;
import uk.firedev.firefly.modules.elevator.ElevatorManager;
import uk.firedev.firefly.modules.kit.KitManager;
import uk.firedev.firefly.modules.nickname.NicknameManager;
import uk.firedev.firefly.modules.small.SmallManager;
import uk.firedev.firefly.modules.titles.TitleManager;
import uk.firedev.firefly.placeholders.MiniPlaceholdersExpansion;
import uk.firedev.firefly.placeholders.PlaceholderAPIExpansion;

import java.util.List;

public final class Firefly extends JavaPlugin {

    private static Firefly instance;
    private static TaskScheduler scheduler;

    @Override
    public void onEnable() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);

        // Load configs
        MessageConfig.getInstance().reload();
        GUIConfig.getInstance().reload();

        registerPermissions();

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

        // Load modules
        SmallManager.getInstance().load();
        if (ModuleConfig.getInstance().elevatorModuleEnabled()) {
            ElevatorManager.getInstance().load();
            Loggers.info(getComponentLogger(), "Loaded Elevator Module.");
        }
        if (ModuleConfig.getInstance().titleModuleEnabled()) {
            TitleManager.getInstance().load();
            Loggers.info(getComponentLogger(), "Loaded Title Module.");
        }
        if (ModuleConfig.getInstance().kitsModuleEnabled()) {
            KitManager.getInstance().load();
            Loggers.info(getComponentLogger(), "Loaded Kits Module.");
        }
        if (ModuleConfig.getInstance().nicknamesModuleEnabled()) {
            NicknameManager.getInstance().load();
            Loggers.info(getComponentLogger(), "Loaded Nicknames Module.");
        }
        if (ModuleConfig.getInstance().aliasesModuleEnabled()) {
            AliasManager.getInstance().load();
            Loggers.info(getComponentLogger(), "Loaded Alias Module.");
        }

    }

    @Override
    public void onDisable() {
        NicknameManager.getInstance().unload();
        Database.getInstance().unload();
    }

    public void reload() {
        MessageConfig.getInstance().reload();
        GUIConfig.getInstance().reload();
        ElevatorManager.getInstance().reload();
        TitleManager.getInstance().reload();
        KitManager.getInstance().reload();
        NicknameManager.getInstance().reload();
        AliasManager.getInstance().reload();
        SmallManager.getInstance().reload();
    }

    public static Firefly getInstance() { return instance; }

    public static TaskScheduler getScheduler() { return scheduler; }

    public boolean isPluginEnabled(String plugin) {
        return getServer().getPluginManager().isPluginEnabled(plugin);
    }

    private void registerPermissions() {
        List<Permission> permissions = List.of(
                new Permission("firefly.command.nickname.bypass.blacklist"),
                new Permission("firefly.command.nickname.bypass.length"),
                new Permission("firefly.command.nickname.colors"),
                new Permission("firefly.command.nickname.unique")
        );
        permissions.forEach(getServer().getPluginManager()::addPermission);
    }

}
