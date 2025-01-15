package uk.firedev.firefly.modules.command;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

import java.util.List;

public class CommandConfig extends Config {

    private static CommandConfig instance;

    private CommandConfig() {
        super("modules/commands.yml", "modules/commands.yml", Firefly.getInstance(), false);
    }

    public static CommandConfig getInstance() {
        if (instance == null) {
            instance = new CommandConfig();
        }
        return instance;
    }

    // Fly Messages

    public ComponentMessage getFlightEnabledMessage() {
        ComponentMessage message = getComponentMessage("fly.messages.enabled", "{prefix}<color:#F0E68C>Flight is now enabled.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getFlightEnabledSenderMessage() {
        ComponentMessage message = getComponentMessage("fly.messages.enabled-sender", "{prefix}<color:#F0E68C>Flight is now enabled for {target}.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getFlightDisabledMessage() {
        ComponentMessage message = getComponentMessage("fly.messages.disabled", "{prefix}<red>Flight is now disabled.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getFlightDisabledSenderMessage() {
        ComponentMessage message = getComponentMessage("fly.messages.disabled-sender", "{prefix}<red>Flight is now disabled for {target}.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    // FlySpeed Messages

    public ComponentMessage getFlySpeedSetMessage() {
        ComponentMessage message = getComponentMessage("flyspeed.messages.set", "{prefix}<color:#F0E68C>Your fly speed has been set to {speed}.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getFlySpeedSetSenderMessage() {
        ComponentMessage message = getComponentMessage("flyspeed.messages.set-sender", "{prefix}<color:#F0E68C>{target}'s fly speed has been set to {speed}.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

}
