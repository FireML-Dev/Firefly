package uk.firedev.firefly.config;

import uk.firedev.daisylib.Config;
import uk.firedev.firefly.Firefly;

public class MainConfig extends Config {

    private static MainConfig instance = null;

    private MainConfig() {
        super("config.yml", "config.yml", Firefly.getInstance(), true);
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
