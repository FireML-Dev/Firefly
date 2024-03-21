package uk.firedev.skylight.config;

import uk.firedev.skylight.Skylight;

public class MainConfig extends uk.firedev.daisylib.Config {

    private static MainConfig instance = null;

    private MainConfig() {
        super("config.yml", Skylight.getInstance(), true);
    }

    public static MainConfig getInstance() {
        if (instance == null) {
            instance = new MainConfig();
        }
        return instance;
    }

}
