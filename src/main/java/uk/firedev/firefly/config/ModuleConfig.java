package uk.firedev.firefly.config;

import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;

public class ModuleConfig extends ConfigBase {

    private static ModuleConfig instance = null;

    private ModuleConfig() {
        super("modules.yml", "modules.yml", Firefly.getInstance());
    }

    public static ModuleConfig getInstance() {
        if (instance == null) {
            instance = new ModuleConfig();
        }
        return instance;
    }

    public boolean isModuleEnabled(@NonNull String module) {
        return getConfig().getBoolean(module.toLowerCase());
    }

}
