package uk.firedev.firefly.modules.messaging.command;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.GreedyStringArgument;
import uk.firedev.firefly.modules.messaging.MessagingConfig;
import uk.firedev.firefly.modules.messaging.MessagingModule;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.utils.StringUtils;
import uk.firedev.messagelib.message.ComponentMessage;

import java.util.Objects;

public class ReplyCommand {

    public static CommandTree getCommand() {
        MessagingConfig config = MessagingConfig.getInstance();
        return new CommandTree(config.getReplyCommandName())
            .withAliases(config.getReplyCommandAliases())
            .withShortDescription("Reply to another player's message.")
            .withPermission(MessagingModule.REPLY_PERMISSION)
            .then(
                new GreedyStringArgument("reply")
                    .executesPlayer(info -> {
                        String str = Objects.requireNonNull(info.args().getUnchecked("reply"));

                        sendMessage(info.sender(), str);
                    })
            );
    }

    private static void sendMessage(@NotNull Player sender, @NotNull String str) {
        Player target = MessagingModule.getInstance().getLastMessage(sender.getUniqueId());
        if (target == null) {
            MessagingConfig.getInstance().getCannotReplyMessage().send(sender);
            return;
        }

        Component message = StringUtils.getColorOnlyComponent(str);
        ComponentMessage msg = MessagingConfig.getInstance().getMessageFormat()
            .replace("sender", getNickname(sender))
            .replace("receiver", getNickname(target))
            .replace("message", message);

        msg.send(sender);
        msg.send(target);
        MessagingModule.getInstance().setLastMessage(target.getUniqueId(), sender.getUniqueId());
    }

    private static @NotNull Component getNickname(@NotNull Player player) {
        if (NicknameModule.getInstance().isLoaded()) {
            return NicknameModule.getInstance().getNickname(player);
        }
        return player.name();
    }

}
