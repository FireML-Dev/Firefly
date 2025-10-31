package uk.firedev.firefly.modules.messaging.command;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.GreedyStringArgument;
import uk.firedev.firefly.modules.messaging.MessagingConfig;
import uk.firedev.firefly.modules.messaging.MessagingModule;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.utils.StringUtils;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;

import java.util.Objects;

public class MessageCommand {

    public static CommandTree getCommand() {
        MessagingConfig config = MessagingConfig.getInstance();
        return new CommandTree(config.getMessageCommandName())
            .withAliases(config.getMessageCommandAliases())
            .withShortDescription("Send a message to another player.")
            .withPermission(MessagingModule.MESSAGE_PERMISSION)
            .thenNested(
                PlayerArgument.create("target"),
                new GreedyStringArgument("message")
                    .executesPlayer(info -> {
                        Player target = Objects.requireNonNull(info.args().getUnchecked("target"));
                        String str = Objects.requireNonNull(info.args().getUnchecked("message"));

                        sendMessage(info.sender(), target, str);
                    })
            );
    }

    private static void sendMessage(@NotNull Player sender, @NotNull Player target, @NotNull String str) {
        Component message = StringUtils.getColorOnlyComponent(str);
        ComponentMessage msg = MessagingConfig.getInstance().getMessageFormat()
            .replace("{sender}", getNickname(sender))
            .replace("{receiver}", getNickname(target))
            .replace("{message}", message);

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
