package uk.firedev.skylight;

import uk.firedev.skylight.config.MainConfig;

public class Modules {

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

    private static Modules instance = null;

    private Modules() {}

    public static Modules getInstance() {
        if (instance == null) {
            instance = new Modules();
        }
        return instance;
    }

}
