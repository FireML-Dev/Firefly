package uk.firedev.skylight;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.UniversalScheduler;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import uk.firedev.skylight.config.ModuleConfig;
import uk.firedev.skylight.database.Database;
import uk.firedev.skylight.modules.nickname.NicknameManager;
import uk.firedev.skylight.modules.titles.TitleManager;
import uk.firedev.skylight.config.GUIConfig;
import uk.firedev.skylight.config.MainConfig;
import uk.firedev.skylight.config.MessageConfig;
import uk.firedev.skylight.modules.elevator.ElevatorManager;
import uk.firedev.skylight.modules.kit.KitManager;
import uk.firedev.skylight.placeholders.MiniPlaceholdersExpansion;
import uk.firedev.skylight.placeholders.PlaceholderAPIExpansion;
import uk.firedev.skylight.modules.small.SmallManager;

import java.util.List;
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

        registerPermissions();

        Database.getInstance().load();
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
        if (ModuleConfig.getInstance().elevatorModuleEnabled()) {
            ElevatorManager.getInstance().load();
            Loggers.log(Level.INFO, getLogger(), "Loaded Elevator Module.");
        }
        if (ModuleConfig.getInstance().titleModuleEnabled()) {
            TitleManager.getInstance().load();
            Loggers.log(Level.INFO, getLogger(), "Loaded Title Module.");
        }
        if (ModuleConfig.getInstance().kitsModuleEnabled()) {
            KitManager.getInstance().load();
            Loggers.log(Level.INFO, getLogger(), "Loaded Kits Module.");
        }
        if (ModuleConfig.getInstance().nicknamesModuleEnabled()) {
            NicknameManager.getInstance().load();
            Loggers.info(getLogger(), "Loaded Nicknames Module.");
        }

    }

    @Override
    public void onDisable() {
        Database.getInstance().unload();
    }

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

    private void registerPermissions() {
        List<Permission> permissions = List.of(
                new Permission("skylight.command.nickname.bypass.blacklist"),
                new Permission("skylight.command.nickname.bypass.length"),
                new Permission("skylight.command.nickname.colors"),
                new Permission("skylight.command.nickname.other")
        );
        permissions.forEach(getServer().getPluginManager()::addPermission);
    }

}
