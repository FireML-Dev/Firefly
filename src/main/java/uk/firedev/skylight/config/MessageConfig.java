package uk.firedev.skylight.config;

import uk.firedev.skylight.Skylight;

public class MessageConfig extends uk.firedev.daisylib.Config implements uk.firedev.daisylib.utils.MessageUtils {

    private static MessageConfig instance = null;

    private MessageConfig() {
        super("messages.yml", Skylight.getInstance());
    }

    public static MessageConfig getInstance() {
        if (instance == null) {
            instance = new MessageConfig();
        }
        return instance;
    }

}
