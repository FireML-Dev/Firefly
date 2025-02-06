package uk.firedev.firefly.modules.protection;

import uk.firedev.firefly.Module;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.ModuleManager;
import uk.firedev.firefly.modules.protection.protections.AmethystProtection;
import uk.firedev.firefly.modules.protection.protections.LootChestProtection;

import java.util.List;

public class ProtectionModule implements Module {

    private static ProtectionModule instance;

    private boolean loaded;
    private final List<SubModule> protections = List.of(
            AmethystProtection.getInstance(),
            LootChestProtection.getInstance()
    );

    private ProtectionModule() {}

    public static ProtectionModule getInstance() {
        if (instance == null) {
            instance = new ProtectionModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "Protection";
    }

    @Override
    public boolean isConfigEnabled() {
        return ModuleConfig.getInstance().isModuleEnabled("protection");
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        ProtectionConfig.getInstance().init();
        protections.forEach(protection -> ModuleManager.getInstance().registerOrUnregisterModule(protection));
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        ProtectionConfig.getInstance().reload();
        protections.forEach(protection -> ModuleManager.getInstance().registerOrUnregisterModule(protection));
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        protections.forEach(SubModule::unregister);
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

}
