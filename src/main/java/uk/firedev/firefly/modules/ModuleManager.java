package uk.firedev.firefly.modules;

import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.command.CommandModule;
import uk.firedev.firefly.modules.elevator.ElevatorModule;
import uk.firedev.firefly.modules.kit.KitModule;
import uk.firedev.firefly.modules.messaging.MessagingModule;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.modules.playtime.PlaytimeModule;
import uk.firedev.firefly.modules.protection.ProtectionModule;
import uk.firedev.firefly.modules.teleportation.TeleportModule;
import uk.firedev.firefly.modules.titles.TitleModule;

import java.util.List;

public class ModuleManager {

    private static ModuleManager instance;
    private boolean loaded;

    private final List<Module> modules = List.of(
        ProtectionModule.getInstance(),
        ElevatorModule.getInstance(),
        TitleModule.getInstance(),
        KitModule.getInstance(),
        NicknameModule.getInstance(),
        PlaytimeModule.getInstance(),
        TeleportModule.getInstance(),
        CommandModule.getInstance(),
        MessagingModule.getInstance()
    );

    private ModuleManager() {}

    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    public void load() {
        if (isLoaded()) {
            return;
        }
        ModuleConfig.getInstance().init();
        ProtectionModule.getInstance().load();
        ElevatorModule.getInstance().load();
        TitleModule.getInstance().load();
        KitModule.getInstance().load();
        NicknameModule.getInstance().load();
        PlaytimeModule.getInstance().load();
        TeleportModule.getInstance().load();
        CommandModule.getInstance().load();
        MessagingModule.getInstance().load();
        loaded = true;
    }

    public void reload() {
        if (!isLoaded()) {
            return;
        }
        ModuleConfig.getInstance().reload();
        ProtectionModule.getInstance().reload();
        ElevatorModule.getInstance().reload();
        TitleModule.getInstance().reload();
        KitModule.getInstance().reload();
        NicknameModule.getInstance().reload();
        PlaytimeModule.getInstance().reload();
        TeleportModule.getInstance().reload();
        CommandModule.getInstance().reload();
        MessagingModule.getInstance().reload();
    }

    public void unload() {
        if (!isLoaded()) {
            return;
        }
        modules.forEach(Module::unload);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public List<Module> getModules() {
        return List.copyOf(modules);
    }

}
