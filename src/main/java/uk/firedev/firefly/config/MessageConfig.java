package uk.firedev.firefly.config;

import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;

import static uk.firedev.daisylib.libs.messagelib.message.ComponentMessage.componentMessage;

public class MessageConfig extends ConfigBase {

    private static MessageConfig instance = null;

    private MessageConfig() {
        super("messages.yml", "messages.yml", Firefly.getInstance());
        init();
    }

    public static MessageConfig getInstance() {
        if (instance == null) {
            instance = new MessageConfig();
        }
        return instance;
    }

    public Replacer getPrefixReplacer() {
        return Replacer.replacer().addReplacement("{prefix}", getPrefix());
    }

    // General Messages

    public ComponentMessage getPrefix() {
        return getComponentMessage("prefix", "<gray>[Firefly]</gray> ");
    }

    public ComponentMessage getPlayerNotFoundMessage() {
        return getComponentMessage("player-not-found", "<red>Player not found.").replace(getPrefixReplacer());
    }

    public ComponentMessage getErrorOccurredMessage() {
        return getComponentMessage("error-occurred", "<red>An error has occurred. Please try again.").replace(getPrefixReplacer());
    }

    public ComponentMessage getFeatureDisabledMessage() {
        return getComponentMessage("feature-disabled", "{prefix}<red>This feature is disabled.").replace(getPrefixReplacer());
    }

    // Main Command Messages

    public ComponentMessage getMainCommandReloadedMessage() {
        return getComponentMessage("main-command.reloaded", "{prefix}<color:#F0E68C>Successfully reloaded the plugin").replace(getPrefixReplacer());
    }

    // Teleport Warmup Messages

    public ComponentMessage getTeleportWarmupCompleteMessage() {
        return getComponentMessage("teleport-warmup.complete", "{prefix}<#F0E68C>Successfully teleported!").replace(getPrefixReplacer());
    }

    public ComponentMessage getTeleportWarmupMessage() {
        return getComponentMessage("teleport-warmup.warmup", "{prefix}<#F0E68C>Teleporting in {time} seconds.").replace(getPrefixReplacer());
    }

    public ComponentMessage getTeleportWarmupCancelledMessage() {
        return getComponentMessage("teleport-warmup.cancelled", "{prefix}<red>Teleportation cancelled.").replace(getPrefixReplacer());
    }

    public ComponentMessage getTeleportWarmupAlreadyTeleportingMessage() {
        return getComponentMessage("teleport-warmup.already-teleporting", "{prefix}<red>You are already teleporting somewhere!").replace(getPrefixReplacer());
    }

}
