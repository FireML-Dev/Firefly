package uk.firedev.firefly.config;

import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;

public class MainConfig extends ConfigBase {

    private static MainConfig instance = null;

    private MainConfig() {
        super("config.yml", "config.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
    }

    public static MainConfig getInstance() {
        if (instance == null) {
            instance = new MainConfig();
        }
        return instance;
    }

    public long getDatabaseSaveInterval() {
        return getConfig().getLong("database-save-interval");
    }

}
