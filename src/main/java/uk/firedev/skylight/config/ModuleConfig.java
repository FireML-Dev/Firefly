package uk.firedev.skylight.config;

import uk.firedev.skylight.Skylight;

public class ModuleConfig extends uk.firedev.daisylib.Config {

    public boolean elevatorModuleEnabled() {
        return getConfig().getBoolean("modules.elevator");
    }

    public boolean titleModuleEnabled() {
        return getConfig().getBoolean("modules.chat.title");
    }

    public boolean amethystProtectionModuleEnabled() {
        return getConfig().getBoolean("modules.small-features.amethyst-protect");
    }

    public boolean lootChestProtectionModuleEnabled() {
        return getConfig().getBoolean("modules.small-features.loot-chest-protection");
    }

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

}
