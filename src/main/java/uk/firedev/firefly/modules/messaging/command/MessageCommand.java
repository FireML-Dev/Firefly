package uk.firedev.firefly.modules.messaging.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.argument.PlayerArgument;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.modules.messaging.MessagingConfig;
import uk.firedev.firefly.modules.messaging.MessagingModule;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.utils.StringUtils;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;

import java.util.List;

public class MessageCommand implements CommandHolder {

    @Override
    public @NonNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal(MessagingConfig.getInstance().getMessageCommandName())
            .requires(stack -> MessagingModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission(permission()))
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .then(
                        Commands.argument("message", StringArgumentType.greedyString())
                            .executes(context -> {
                                Player player = CommandUtils.requirePlayer(context.getSource());
                                if (player == null) {
                                    return 1;
                                }
                                Player target = context.getArgument("target", Player.class);
                                String string = context.getArgument("message", String.class);
                                sendMessage(player, target, string);
                                return 1;
                            })
                    )
            )
            .build();
    }

    /**
     * @return The list of aliases this command should have.
     */
    @NonNull
    @Override
    public List<String> aliases() {
        return MessagingConfig.getInstance().getMessageCommandAliases();
    }

    /**
     * @return The permission for executing this command on yourself.
     */
    @NonNull
    @Override
    public String permission() {
        return "firefly.command.message";
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @NonNull
    @Override
    public String targetPermission() {
        return "firefly.command.message";
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    public String description() {
        return null;
    }

    private void sendMessage(@NonNull Player sender, @NonNull Player target, @NonNull String str) {
        Component message = StringUtils.getColorOnlyComponent(str);
        ComponentMessage msg = MessagingConfig.getInstance().getMessageFormat()
            .replace("{sender}", getNickname(sender))
            .replace("{receiver}", getNickname(target))
            .replace("{message}", message);

        msg.send(sender);
        msg.send(target);
        MessagingModule.getInstance().setLastMessage(target.getUniqueId(), sender.getUniqueId());
    }

    private @NonNull Component getNickname(@NonNull Player player) {
        if (NicknameModule.getInstance().isConfigEnabled()) {
            return NicknameModule.getInstance().getNickname(player);
        }
        return player.name();
    }

}
