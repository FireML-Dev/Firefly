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
        return getConfig().getBoolean("modules.amethyst-protect");
    }

    private static ModuleConfig instance = null;

    private ModuleConfig() {
        super("modules.yml", Skylight.getInstance());
    }

    public static ModuleConfig getInstance() {
        if (instance == null) {
            instance = new ModuleConfig();
        }
        return instance;
    }

}
