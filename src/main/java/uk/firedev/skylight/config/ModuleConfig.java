package uk.firedev.skylight.config;

import uk.firedev.skylight.Skylight;

public class ModuleConfig extends uk.firedev.daisylib.Config {

    private static ModuleConfig instance = null;

    private ModuleConfig() {
        super("modules.yml", Skylight.getInstance(), true);
    }

    public static ModuleConfig getInstance() {
        if (instance == null) {
            instance = new ModuleConfig();
        }
        return instance;
    }

    public boolean elevatorModuleEnabled() {
        return MainConfig.getInstance().getConfig().getBoolean("elevator.enabled");
    }

    public boolean titleModuleEnabled() {
        return MainConfig.getInstance().getConfig().getBoolean("title.enabled");
    }

    public boolean amethystProtectionModuleEnabled() {
        return MainConfig.getInstance().getConfig().getBoolean("amethyst-protection.enabled");
    }

    public boolean lootChestProtectionModuleEnabled() {
        return MainConfig.getInstance().getConfig().getBoolean("loot-chest-protection.enabled");
    }

    public boolean kitsModuleEnabled() {
        return MainConfig.getInstance().getConfig().getBoolean("kits.enabled");
    }

}
