package uk.firedev.firefly.modules.messaging;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;

import java.util.List;

public class MessagingConfig extends ConfigBase {

    private static MessagingConfig instance;

    private @NotNull String messageCommandName = "message";
    private @NotNull List<String> messageCommandAliases = List.of();

    private @NotNull String replyCommandName = "reply";
    private @NotNull List<String> replyCommandAliases = List.of();

    private MessagingConfig() {
        super("modules/messaging.yml", "modules/messaging.yml", Firefly.getInstance());
        init();

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

    public @NotNull String getMessageCommandName() {
        return messageCommandName;
    }

    public @NotNull List<String> getMessageCommandAliases() {
        return messageCommandAliases;
    }

    public @NotNull String getReplyCommandName() {
        return replyCommandName;
    }

    public @NotNull List<String> getReplyCommandAliases() {
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
