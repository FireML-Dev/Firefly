package uk.firedev.firefly.modules;

import org.jetbrains.annotations.NotNull;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.SubModule;
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
        modules.forEach(this::registerOrUnregisterModule);
        loaded = true;
    }

    public void reload() {
        if (!isLoaded()) {
            return;
        }
        ModuleConfig.getInstance().reload();
        modules.forEach(this::registerOrUnregisterModule);
    }

    public void unload() {
        if (!isLoaded()) {
            return;
        }
        modules.forEach(Module::unregister);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public List<Module> getModules() {
        return List.copyOf(modules);
    }

    public void registerOrUnregisterModule(@NotNull SubModule module) {
        if (module.isConfigEnabled()) {
            module.register();
        } else {
            module.unregister();
        }
        // Ensure we reload after this
        module.reload();
    }

}
