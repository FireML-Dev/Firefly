package uk.firedev.firefly.modules.messaging;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

import java.util.Arrays;
import java.util.List;

public class MessagingConfig extends ConfigBase {

    private static MessagingConfig instance;

    private final @NotNull String messageCommandName;
    private final @NotNull String[] messageCommandAliases;

    private final @NotNull String replyCommandName;
    private final @NotNull String[] replyCommandAliases;

    private MessagingConfig() {
        super("modules/messaging.yml", "modules/messaging.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();

        this.messageCommandName = getConfig().getString("message-command.name", "message");
        this.messageCommandAliases = getConfig().getStringList("message-command.aliases").toArray(String[]::new);

        this.replyCommandName = getConfig().getString("reply-command.name", "reply");
        this.replyCommandAliases = getConfig().getStringList("reply-command.aliases").toArray(String[]::new);
    }

    public static MessagingConfig getInstance() {
        if (instance == null) {
            instance = new MessagingConfig();
        }
        return instance;
    }

    // Commands

    public @NotNull String getMessageCommandName() {
        return messageCommandName;
    }

    public @NotNull String[] getMessageCommandAliases() {
        return messageCommandAliases;
    }

    public @NotNull String getReplyCommandName() {
        return replyCommandName;
    }

    public @NotNull String[] getReplyCommandAliases() {
        return replyCommandAliases;
    }

    // Messages

    public ComponentMessage getMessageFormat() {
        ComponentMessage message = getComponentMessage(
            "messages.format",
            "<gray>[<white>{sender}</white> -> <white>{receiver}</white>]</gray> <white>{message}"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCannotReplyMessage() {
        ComponentMessage message = getComponentMessage(
            "messages.cannot-reply",
            "<red>There is nobody to reply to!</red>"
        );
        return message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
    }

}
