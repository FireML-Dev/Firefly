package uk.firedev.skylight.config;

import uk.firedev.skylight.Skylight;

public class ModuleConfig extends uk.firedev.daisylib.Config {

    private static ModuleConfig instance = null;

    private ModuleConfig() {
        super("modules.yml", Skylight.getInstance(), true, true);
    }

    public static ModuleConfig getInstance() {
        if (instance == null) {
            instance = new ModuleConfig();
        }
        return instance;
    }

    public boolean elevatorModuleEnabled() {
        return getConfig().getBoolean("modules.elevators");
    }

    public boolean titleModuleEnabled() {
        return getConfig().getBoolean("modules.titles");
    }

    public boolean amethystProtectionModuleEnabled() {
        return getConfig().getBoolean("modules.small.amethyst-protection");
    }

    public boolean lootChestProtectionModuleEnabled() {
        return getConfig().getBoolean("modules.small.loot-chest-protection");
    }

    public boolean kitsModuleEnabled() {
        return getConfig().getBoolean("modules.kits");
    }

    public boolean nicknamesModuleEnabled() {
        return getConfig().getBoolean("modules.nicknames");
    }

}
