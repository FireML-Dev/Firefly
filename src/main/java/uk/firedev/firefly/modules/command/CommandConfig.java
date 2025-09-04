package uk.firedev.firefly.modules.command;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.messagelib.message.ComponentMessage;

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

    // Ride Messages

    public ComponentMessage getRideTargetNotFoundMessage() {
        return getComponentMessage("ride.messages.target-not-found", "{prefix}<color:#F0E68C>{target}'s fly speed has been set to {speed}.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getRideNotPermittedMessage() {
        return getComponentMessage("ride.messages.not-permitted", "{prefix}<red>You are not allowed to ride this entity!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getRideRidingMessage() {
        return getComponentMessage("ride.messages.riding", "{prefix}<#F0E68C>You are now riding an entity. Sneak to dismount.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getRideRidingSenderMessage() {
        return getComponentMessage("ride.messages.riding-sender", "{prefix}<#F0E68C>{target} is now riding an entity.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getRideShakeMessage() {
        return getComponentMessage("ride.messages.shake", "{prefix}<#F0E68C>{player} is now riding you. Type <gold><click:run_command:'/ride shake'>/ride shake</click> <#F0E68C>to get them off!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getRideShookMessage() {
        return getComponentMessage("ride.messages.shook", "{prefix}<#F0E68C>Successfully shook off all players!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    // ItemFrame Messages

    public ComponentMessage getItemFrameLookAtFrameMessage() {
        return getComponentMessage("itemframe.messages.look-at-frame", "{prefix}<red>You must be looking at an item frame!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getItemFrameInvisibleOnMessage() {
        return getComponentMessage("itemframe.messages.invisible-on", "{prefix}<#F0E68C>Item Frame is now invisible!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getItemFrameInvisibleOffMessage() {
        return getComponentMessage("itemframe.messages.invisible-off", "{prefix}<#F0E68C>Item Frame is no longer invisible!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getItemFrameFixedOnMessage() {
        return getComponentMessage("itemframe.messages.fixed-on", "{prefix}<#F0E68C>Item Frame is now fixed!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getItemFrameFixedOffMessage() {
        return getComponentMessage("itemframe.messages.fixed-off", "{prefix}<#F0E68C>Item Frame is no longer fixed!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getItemFrameInvulnerableOnMessage() {
        return getComponentMessage("itemframe.messages.invulnerable-on", "{prefix}<#F0E68C>Item Frame is now invulnerable!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getItemFrameInvulnerableOffMessage() {
        return getComponentMessage("itemframe.messages.invulnerable-off", "{prefix}<#F0E68C>Item Frame is no longer invulnerable!").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    // Godmode Messages

    public ComponentMessage getGodmodeEnabledMessage() {
        return getComponentMessage("godmode.messages.enabled", "{prefix}<color:#F0E68C>Godmode is now enabled.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getGodmodeEnabledSenderMessage() {
        return getComponentMessage("godmode.messages.enabled-sender", "{prefix}<color:#F0E68C>Godmode is now enabled for {target}.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getGodmodeDisabledMessage() {
        return getComponentMessage("godmode.messages.disabled", "{prefix}<red>Godmode is now disabled.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getGodmodeDisabledSenderMessage() {
        return getComponentMessage("godmode.messages.disabled-sender", "{prefix}<red>Godmode is now disabled for {target}.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public boolean getGodmodePreventHunger() {
        return getConfig().getBoolean("godmode.prevent-hunger", true);
    }

    // Heal Messages

    public ComponentMessage getHealedMessage() {
        return getComponentMessage("heal.messages.healed", "{prefix}<color:#F0E68C>You have been healed.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getHealedSenderMessage() {
        return getComponentMessage("heal.messages.healed-sender", "{prefix}<color:#F0E68C>You have healed {target}.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    // Rename Messages

    public ComponentMessage getRenameHoldItemMessage() {
        return getComponentMessage("rename.messages.hold-an-item", "{prefix}<red>Please hold an item.").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getRenamedMessage() {
        return getComponentMessage("rename.messages.renamed", "{prefix}<#F0E68C>Renamed your item to {newName}").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getRenamedSenderMessage() {
        return getComponentMessage("rename.messages.renamed-sender", "{prefix}<#F0E68C>Renamed {target}'s item to {newName}").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

}
