package uk.firedev.firefly.modules.command;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;

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

    // Fly Messages
    
    private ComponentMessage get(@NotNull String path, @NotNull String def) {
        return getComponentMessage(path, def).replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getFlightEnabledMessage() {
        return getComponentMessage("fly.messages.enabled", "{prefix}<color:#F0E68C>Flight is now enabled.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getFlightEnabledSenderMessage() {
        return getComponentMessage("fly.messages.enabled-sender", "{prefix}<color:#F0E68C>Flight is now enabled for {target}.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getFlightDisabledMessage() {
        return getComponentMessage("fly.messages.disabled", "{prefix}<red>Flight is now disabled.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getFlightDisabledSenderMessage() {
        return getComponentMessage("fly.messages.disabled-sender", "{prefix}<red>Flight is now disabled for {target}.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    // FlySpeed Messages

    public ComponentMessage getFlySpeedSetMessage() {
        return getComponentMessage("flyspeed.messages.set", "{prefix}<color:#F0E68C>Your fly speed has been set to {speed}.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getFlySpeedSetSenderMessage() {
        return getComponentMessage("flyspeed.messages.set-sender", "{prefix}<color:#F0E68C>{target}'s fly speed has been set to {speed}.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

}
