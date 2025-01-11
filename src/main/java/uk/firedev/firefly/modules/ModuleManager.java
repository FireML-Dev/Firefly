package uk.firedev.firefly.modules;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.customcommands.CustomCommandsModule;
import uk.firedev.firefly.modules.elevator.ElevatorModule;
import uk.firedev.firefly.modules.kit.KitModule;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.modules.playtime.PlaytimeModule;
import uk.firedev.firefly.modules.small.AmethystProtection;
import uk.firedev.firefly.modules.small.LootChestProtection;
import uk.firedev.firefly.modules.teleportation.TeleportModule;
import uk.firedev.firefly.modules.titles.TitleModule;

import java.util.HashMap;
import java.util.Map;

public class ModuleManager {

    private static ModuleManager instance;
    private boolean loaded;
    private final Map<String, Module> loadedModules;

    private ModuleManager() {
        loadedModules = new HashMap<>();
    }

    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    public boolean registerModule(@NotNull Module module) {
        String identifier = module.getIdentifier();
        if (loadedModules.containsKey(identifier)) {
            return false;
        }
        module.load();
        loadedModules.put(identifier, module);
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Loaded " + module.getIdentifier() + " Module.");
        return true;
    }

    public void unregisterModule(@NotNull String moduleName) {
        Module module = loadedModules.get(moduleName);
        if (module != null) {
            module.unload();
            loadedModules.remove(moduleName);
        }
    }

    public void load() {
        if (isLoaded()) {
            return;
        }
        ModuleConfig moduleConfig = ModuleConfig.getInstance();

        // Register internal modules

        if (moduleConfig.amethystProtectionModuleEnabled()) {
            AmethystProtection.getInstance().register();
        }
        if (moduleConfig.lootChestProtectionModuleEnabled()) {
            LootChestProtection.getInstance().register();
        }
        if (moduleConfig.elevatorModuleEnabled()) {
            ElevatorModule.getInstance().register();
        }
        if (moduleConfig.titleModuleEnabled()) {
            TitleModule.getInstance().register();
        }
        if (moduleConfig.kitsModuleEnabled()) {
            KitModule.getInstance().register();
        }
        if (moduleConfig.nicknamesModuleEnabled()) {
            NicknameModule.getInstance().register();
        }
        if (moduleConfig.aliasesModuleEnabled()) {
            CustomCommandsModule.getInstance().register();
        }
        if (moduleConfig.playtimeModuleEnabled()) {
            PlaytimeModule.getInstance().register();
        }
        if (moduleConfig.teleportationModuleEnabled()) {
            TeleportModule.getInstance().register();
        }
        loaded = true;
    }

    public void reload() {
        if (!isLoaded()) {
            return;
        }
        loadedModules.values().forEach(Module::reload);
    }

    public void unload() {
        if (!isLoaded()) {
            return;
        }
        Map<String, Module> loadedModulesCopy = Map.copyOf(loadedModules);
        loadedModulesCopy.values().forEach(Module::unregister);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public Map<String, Module> getLoadedModules() {
        return Map.copyOf(loadedModules);
    }

}
