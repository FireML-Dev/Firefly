package uk.firedev.firefly.modules.command;

import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;

public class CommandConfig extends ConfigBase {

    private static CommandConfig instance;

    private CommandConfig() {
        super("modules/commands.yml", "modules/commands.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
    }

    public static CommandConfig getInstance() {
        if (instance == null) {
            instance = new CommandConfig();
        }
        return instance;
    }

}
