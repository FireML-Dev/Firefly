package uk.firedev.firefly.modules.command;

import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

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

    // Ride Messages

    public ComponentMessage getRideTargetNotFoundMessage() {
        ComponentMessage message = getComponentMessage("ride.messages.target-not-found", "{prefix}<color:#F0E68C>{target}'s fly speed has been set to {speed}.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getRideNotPermittedMessage() {
        ComponentMessage message = getComponentMessage("ride.messages.not-permitted", "{prefix}<red>You are not allowed to ride this entity!");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getRideRidingMessage() {
        ComponentMessage message = getComponentMessage("ride.messages.riding", "{prefix}<#F0E68C>You are now riding an entity. Sneak to dismount.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getRideRidingSenderMessage() {
        ComponentMessage message = getComponentMessage("ride.messages.riding-sender", "{prefix}<#F0E68C>{target} is now riding an entity.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getRideShakeMessage() {
        ComponentMessage message = getComponentMessage("ride.messages.shake", "{prefix}<#F0E68C>{player} is now riding you. Type <gold><click:run_command:'/ride shake'>/ride shake</click> <#F0E68C>to get them off!");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getRideShookMessage() {
        ComponentMessage message = getComponentMessage("ride.messages.shook", "{prefix}<#F0E68C>Successfully shook off all players!");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    // ItemFrame Messages

    public ComponentMessage getItemFrameLookAtFrameMessage() {
        ComponentMessage message = getComponentMessage("itemframe.messages.look-at-frame", "{prefix}<red>You must be looking at an item frame!");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getItemFrameInvisibleOnMessage() {
        ComponentMessage message = getComponentMessage("itemframe.messages.invisible-on", "{prefix}<#F0E68C>Item Frame is now invisible!");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getItemFrameInvisibleOffMessage() {
        ComponentMessage message = getComponentMessage("itemframe.messages.invisible-off", "{prefix}<#F0E68C>Item Frame is no longer invisible!");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getItemFrameFixedOnMessage() {
        ComponentMessage message = getComponentMessage("itemframe.messages.fixed-on", "{prefix}<#F0E68C>Item Frame is now fixed!");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getItemFrameFixedOffMessage() {
        ComponentMessage message = getComponentMessage("itemframe.messages.fixed-off", "{prefix}<#F0E68C>Item Frame is no longer fixed!");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getItemFrameInvulnerableOnMessage() {
        ComponentMessage message = getComponentMessage("itemframe.messages.invulnerable-on", "{prefix}<#F0E68C>Item Frame is now invulnerable!");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getItemFrameInvulnerableOffMessage() {
        ComponentMessage message = getComponentMessage("itemframe.messages.invulnerable-off", "{prefix}<#F0E68C>Item Frame is no longer invulnerable!");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    // Godmode Messages

    public ComponentMessage getGodmodeEnabledMessage() {
        ComponentMessage message = getComponentMessage("godmode.messages.enabled", "{prefix}<color:#F0E68C>Godmode is now enabled.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getGodmodeEnabledSenderMessage() {
        ComponentMessage message = getComponentMessage("godmode.messages.enabled-sender", "{prefix}<color:#F0E68C>Godmode is now enabled for {target}.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getGodmodeDisabledMessage() {
        ComponentMessage message = getComponentMessage("godmode.messages.disabled", "{prefix}<red>Godmode is now disabled.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getGodmodeDisabledSenderMessage() {
        ComponentMessage message = getComponentMessage("godmode.messages.disabled-sender", "{prefix}<red>Godmode is now disabled for {target}.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    // Heal Messages

    public ComponentMessage getHealedMessage() {
        ComponentMessage message = getComponentMessage("heal.messages.healed", "{prefix}<color:#F0E68C>You have been healed.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getHealedSenderMessage() {
        ComponentMessage message = getComponentMessage("heal.messages.healed-sender", "{prefix}<color:#F0E68C>You have healed {target}.");
        message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

}
