package uk.firedev.firefly.config;

import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;

public class MessageConfig extends ConfigBase {

    private static MessageConfig instance = null;

    private MessageConfig() {
        super("messages.yml", "messages.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
    }

    public static MessageConfig getInstance() {
        if (instance == null) {
            instance = new MessageConfig();
        }
        return instance;
    }

    public ComponentReplacer getPrefixReplacer() {
        return ComponentReplacer.create("prefix", getPrefix().getMessage());
    }

    // GENERAL MESSAGES

    public ComponentMessage getPrefix() {
        return ComponentMessage.fromConfig(getConfig(), "prefix", "<gray>[Firefly]</gray> ");
    }

    public ComponentMessage getPlayerNotFoundMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "player-not-found", "<red>Player not found.");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getErrorOccurredMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "error-occurred", "<red>An error has occurred. Please try again.");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    public ComponentMessage getFeatureDisabledMessage() {
        ComponentMessage message = getComponentMessage("feature-disabled", "{prefix}<red>This feature is disabled.");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

    // MAIN COMMAND MESSAGES

    public ComponentMessage getMainCommandReloadedMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "main-command.reloaded", "<color:#F0E68C>Successfully reloaded the plugin");
        message = message.applyReplacer(getPrefixReplacer());
        return message;
    }

}
