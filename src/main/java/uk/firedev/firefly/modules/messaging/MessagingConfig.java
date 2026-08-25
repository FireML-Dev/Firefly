package uk.firedev.firefly.modules.messaging;

import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.BasicConfig;
import uk.firedev.daisylib.messages.message.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

import java.util.List;

public class MessagingConfig extends BasicConfig {

    private static MessagingConfig instance;

    private final @NonNull String messageCommandName;
    private final @NonNull List<String> messageCommandAliases;

    private final @NonNull String replyCommandName;
    private final @NonNull List<String> replyCommandAliases;

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

    public ComponentMessage<?, ?> getMessageFormat() {
        return getComponentMessage(
            "messages.format",
            "<gray>[<white>{sender}</white> -> <white>{receiver}</white>]</gray> <white>{message}"
        );
    }

    public ComponentMessage<?, ?> getCannotReplyMessage() {
        return getComponentMessage(
            "messages.cannot-reply",
            "<red>There is nobody to reply to!</red>"
        );
    }

    @Override
    public ComponentMessage<?, ?> getComponentMessage(@NonNull String path, @NonNull Object def) {
        return super.getComponentMessage(path, def).replace("{prefix}", MessageConfig.getInstance().getPrefix());
    }

}
