package uk.firedev.skylight;

import uk.firedev.skylight.config.MainConfig;

public class Modules {

    public static boolean elevatorModuleEnabled() {
        return MainConfig.getInstance().getConfig().getBoolean("elevator.enabled");
    }

    public static boolean titleModuleEnabled() {
        return MainConfig.getInstance().getConfig().getBoolean("title.enabled");
    }

    public static boolean amethystProtectionModuleEnabled() {
        return MainConfig.getInstance().getConfig().getBoolean("amethyst-protection.enabled");
    }

    public static boolean lootChestProtectionModuleEnabled() {
        return MainConfig.getInstance().getConfig().getBoolean("loot-chest-protection.enabled");
    }

}
