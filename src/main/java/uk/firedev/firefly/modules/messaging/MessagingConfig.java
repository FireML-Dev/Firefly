package uk.firedev.firefly.modules.messaging;

import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;

import java.util.List;

public class MessagingConfig extends ConfigBase {

    private static MessagingConfig instance;

    private @NonNull String messageCommandName = "message";
    private @NonNull List<String> messageCommandAliases = List.of();

    private @NonNull String replyCommandName = "reply";
    private @NonNull List<String> replyCommandAliases = List.of();

    private MessagingConfig() {
        super("modules/messaging.yml", "modules/messaging.yml", Firefly.getInstance());

        this.messageCommandName = getConfig().getString("message-command.name", "message");
        this.messageCommandAliases = getConfig().getStringList("message-command.aliases");

        this.replyCommandName = getConfig().getString("reply-command.name", "reply");
        this.replyCommandAliases = getConfig().getStringList("reply-command.aliases");
    }

    public static MessagingConfig getInstance() {
        if (instance == null) {
            instance = new MessagingConfig();
        }
        return instance;
    }

    // Commands

    public @NonNull String getMessageCommandName() {
        return messageCommandName;
    }

    public @NonNull List<String> getMessageCommandAliases() {
        return messageCommandAliases;
    }

    public @NonNull String getReplyCommandName() {
        return replyCommandName;
    }

    public @NonNull List<String> getReplyCommandAliases() {
        return replyCommandAliases;
    }

    // Messages

    public ComponentMessage getMessageFormat() {
        return getComponentMessage(
            "messages.format",
            "<gray>[<white>{sender}</white> -> <white>{receiver}</white>]</gray> <white>{message}"
        ).replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCannotReplyMessage() {
        return getComponentMessage(
            "messages.cannot-reply",
            "<red>There is nobody to reply to!</red>"
        ).replace(MessageConfig.getInstance().getPrefixReplacer());
    }

}
