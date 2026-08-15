package uk.firedev.firefly.config;

import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.BasicConfig;
import uk.firedev.daisylib.messages.message.ComponentSingleMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.daisylib.messages.message.ComponentMessage;

public class MessageConfig extends BasicConfig {

    private static MessageConfig instance = null;

    private MessageConfig() {
        super("messages.yml", "messages.yml", Firefly.getInstance());
    }

    public static MessageConfig getInstance() {
        if (instance == null) {
            instance = new MessageConfig();
        }
        return instance;
    }

    // General Messages

    public ComponentSingleMessage getPrefix() {
        return super.getComponentMessage("prefix", "<gray>[Firefly]</gray> ").toSingleMessage();
    }

    public ComponentMessage<?, ?>  getPlayerNotFoundMessage() {
        return getComponentMessage("player-not-found", "<red>Player not found.");
    }

    public ComponentMessage<?, ?>  getErrorOccurredMessage() {
        return getComponentMessage("error-occurred", "<red>An error has occurred. Please try again.");
    }

    public ComponentMessage<?, ?>  getFeatureDisabledMessage() {
        return getComponentMessage("feature-disabled", "{prefix}<red>This feature is disabled.");
    }

    // Main Command Messages

    public ComponentMessage<?, ?>  getMainCommandReloadedMessage() {
        return getComponentMessage("main-command.reloaded", "{prefix}<color:#F0E68C>Successfully reloaded the plugin");
    }

    // Teleport Warmup Messages

    public ComponentMessage<?, ?>  getTeleportWarmupCompleteMessage() {
        return getComponentMessage("teleport-warmup.complete", "{prefix}<#F0E68C>Successfully teleported!");
    }

    public ComponentMessage<?, ?>  getTeleportWarmupMessage() {
        return getComponentMessage("teleport-warmup.warmup", "{prefix}<#F0E68C>Teleporting in {time} seconds.");
    }

    public ComponentMessage<?, ?>  getTeleportWarmupCancelledMessage() {
        return getComponentMessage("teleport-warmup.cancelled", "{prefix}<red>Teleportation cancelled.");
    }

    public ComponentMessage<?, ?>  getTeleportWarmupAlreadyTeleportingMessage() {
        return getComponentMessage("teleport-warmup.already-teleporting", "{prefix}<red>You are already teleporting somewhere!");
    }

    @Override
    public ComponentMessage<?, ?> getComponentMessage(@NonNull String path, @NonNull Object def) {
        return super.getComponentMessage(path, def).replace("{prefix}", getPrefix());
    }

}
