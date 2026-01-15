package uk.firedev.firefly.modules;

import org.jetbrains.annotations.NotNull;
import uk.firedev.firefly.Module;
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
        modules.forEach(this::loadModule);
        loaded = true;
    }

    private void loadModule(@NotNull Module module) {
        module.load();
    }

    public void reload() {
        if (!isLoaded()) {
            return;
        }
        modules.forEach(Module::reload);
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
