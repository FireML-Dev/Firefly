package uk.firedev.firefly.modules.protection;

import io.papermc.paper.command.brigadier.Commands;
import org.jspecify.annotations.NonNull;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.ModuleManager;
import uk.firedev.firefly.modules.protection.protections.AmethystProtection;
import uk.firedev.firefly.modules.protection.protections.LootChestProtection;

import java.util.List;

public class ProtectionModule implements Module {

    private static ProtectionModule instance;

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
    public void init() {
        protections.forEach(SubModule::load);
    }

    @Override
    public void reload() {
        ProtectionConfig.getInstance().reload();
        protections.forEach(SubModule::reload);
    }

    @Override
    public void unload() {
        protections.forEach(SubModule::unload);
    }

}
