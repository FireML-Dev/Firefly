package uk.firedev.firefly.modules.messaging.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.firefly.modules.messaging.MessagingConfig;
import uk.firedev.firefly.modules.messaging.MessagingModule;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.utils.StringUtils;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;

public class ReplyCommand {

    // TODO aliases.
    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal(MessagingConfig.getInstance().getReplyCommandName())
            .requires(stack -> MessagingModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission(MessagingModule.REPLY_PERMISSION))
            .then(
                Commands.argument("message", StringArgumentType.greedyString())
                    .executes(context -> {
                        Player player = CommandUtils.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        String string = context.getArgument("message", String.class);
                        sendMessage(player, string);
                        return 1;
                    })
            )
            .build();
    }

    private void sendMessage(@NotNull Player sender, @NotNull String str) {
        Player target = MessagingModule.getInstance().getLastMessage(sender.getUniqueId());
        if (target == null) {
            MessagingConfig.getInstance().getCannotReplyMessage().send(sender);
            return;
        }

        Component message = StringUtils.getColorOnlyComponent(str);
        ComponentMessage msg = MessagingConfig.getInstance().getMessageFormat()
            .replace("{sender}", getNickname(sender))
            .replace("{receiver}", getNickname(target))
            .replace("{message}", message);

        msg.send(sender);
        msg.send(target);
        MessagingModule.getInstance().setLastMessage(target.getUniqueId(), sender.getUniqueId());
    }

    private @NotNull Component getNickname(@NotNull Player player) {
        if (NicknameModule.getInstance().isConfigEnabled()) {
            return NicknameModule.getInstance().getNickname(player);
        }
        return player.name();
    }

}
