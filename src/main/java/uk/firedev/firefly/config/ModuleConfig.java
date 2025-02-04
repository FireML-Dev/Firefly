package uk.firedev.firefly.config;

import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;

public class ModuleConfig extends ConfigBase {

    private static ModuleConfig instance = null;

    private ModuleConfig() {
        super("modules.yml", "modules.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
    }

    public static ModuleConfig getInstance() {
        if (instance == null) {
            instance = new ModuleConfig();
        }
        return instance;
    }

    public boolean elevatorModuleEnabled() {
        return getConfig().getBoolean("elevators");
    }

    public boolean titleModuleEnabled() {
        return getConfig().getBoolean("titles");
    }

    public boolean protectionModuleEnabled() {
        return getConfig().getBoolean("protection");
    }

    public boolean kitsModuleEnabled() {
        return getConfig().getBoolean("kits");
    }

    public boolean nicknamesModuleEnabled() {
        return getConfig().getBoolean("nicknames");
    }

    public boolean aliasesModuleEnabled() {
        return getConfig().getBoolean("aliases");
    }

    public boolean playtimeModuleEnabled() {
        return getConfig().getBoolean("playtime");
    }

    public boolean teleportationModuleEnabled() {
        return getConfig().getBoolean("teleportation");
    }

    public boolean commandsModuleEnabled() {
        return getConfig().getBoolean("commands");
    }

}
