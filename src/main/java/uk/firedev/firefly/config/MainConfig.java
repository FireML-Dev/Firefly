package uk.firedev.firefly.config;

import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;

import java.time.Duration;

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

    public int getDatabaseSaveInterval() {
        return getConfig().getInt("database-save-interval");
    }

    public Duration getDatabaseCacheInterval() {
        return Duration.ofSeconds(
            getConfig().getInt("database-cache-interval")
        );
    }

}
