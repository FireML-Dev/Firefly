package uk.firedev.firefly.modules.nickname.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.OfflinePlayerArgument;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.firefly.modules.nickname.NicknameConfig;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.utils.StringUtils;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;

import java.util.Objects;

public class NicknameCommand {

    // TODO /nick alias.
    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("nickname")
            .requires(stack -> stack.getSender().hasPermission(NicknameModule.COMMAND_PERMISSION))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                checkNickname(player, player);
                return 1;
            })
            .then(check())
            .then(set())
            .then(setOther())
            .then(remove())
            .build();
    }

    private ArgumentBuilder<CommandSourceStack, ?> check() {
        return Commands.literal("check")
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                checkNickname(player, player);
                return 1;
            })
            .then(
                Commands.argument("target", OfflinePlayerArgument.create(PlayerHelper::hasPlayerBeenOnServer))
                    .executes(context -> {
                        OfflinePlayer target = context.getArgument("target", OfflinePlayer.class);
                        checkNickname(context.getSource().getSender(), target);
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> remove() {
        return Commands.literal("remove")
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                NicknameModule.getInstance().removeNickname(player);
                NicknameConfig.getInstance().getCommandRemovedNicknameMessage().send(player);
                return 1;
            })
            .then(
                Commands.argument("target", OfflinePlayerArgument.create(PlayerHelper::hasPlayerBeenOnServer))
                    .requires(stack -> stack.getSender().hasPermission(NicknameModule.COMMAND_PERMISSION_ADMIN))
                    .executes(context -> {
                        OfflinePlayer target = context.getArgument("target", OfflinePlayer.class);

                        // Remove nickname
                        NicknameModule.getInstance().removeNickname(target);

                        // If target is online, tell them
                        Player onlineTarget = target.getPlayer();
                        if (onlineTarget != null) {
                            NicknameConfig.getInstance().getCommandRemovedNicknameMessage().send(onlineTarget);
                        }

                        CommandSender sender = context.getSource().getSender();
                        // If target isn't the sender, tell them
                        if (target != sender) {
                            NicknameConfig.getInstance().getCommandAdminRemovedNicknameMessage().send(sender);
                        }
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set")
            .then(
                Commands.argument("nickname", StringArgumentType.greedyString())
                    .executes(context -> {
                        Player player = CommandUtils.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        String nickname = context.getArgument("nickname", String.class).split(" ")[0]; // Only use the name before the first space
                        Component componentNickname = StringUtils.getColorOnlyComponent(nickname);
                        if (!validateNickname(player, componentNickname)) {
                            return 1;
                        }
                        NicknameModule.getInstance().setNickname(player, nickname);
                        NicknameConfig.getInstance().getCommandSetOwnNicknameMessage()
                            .replace("{nickname}", componentNickname)
                            .send(player);
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> setOther() {
        return Commands.literal("setOther")
            .requires(stack -> stack.getSender().hasPermission(NicknameModule.COMMAND_PERMISSION_ADMIN))
            .then(
                Commands.argument("target", OfflinePlayerArgument.create(PlayerHelper::hasPlayerBeenOnServer))
                    .then(
                        Commands.argument("nickname", StringArgumentType.greedyString())
                            .executes(context -> {
                                OfflinePlayer target = context.getArgument("target", OfflinePlayer.class);
                                String nickname = context.getArgument("nickname", String.class).split(" ")[0]; // Only use the name before the first space
                                Component componentNickname = StringUtils.getColorOnlyComponent(nickname);
                                NicknameModule.getInstance().setNickname(target, nickname);
                                // If target is online, tell them
                                Player onlineTarget = target.getPlayer();
                                if (onlineTarget != null) {
                                    NicknameConfig.getInstance().getCommandSetOwnNicknameMessage()
                                        .replace("{nickname}", componentNickname)
                                        .send(onlineTarget);
                                }

                                CommandSender sender = context.getSource().getSender();
                                // If target isn't the sender, tell them
                                if (target != sender) {
                                    NicknameConfig.getInstance().getCommandAdminSetNicknameMessage()
                                        .replace("{target}", Objects.requireNonNull(target.getName()))
                                        .replace("{nickname}", componentNickname)
                                        .send(sender);
                                }
                                return 1;
                            })
                    )
            );
    }

    // Convenience

    private void checkNickname(@NotNull CommandSender sender, @NotNull OfflinePlayer target) {
        Component nickname = NicknameModule.getInstance().getNickname(target);
        NicknameConfig.getInstance().getCommandCheckInfoMessage()
            .replace("{nickname}", nickname)
            .replace("{player}", Objects.requireNonNullElse(target.getName(), "N/A"))
            .send(sender);
    }

    private boolean validateNickname(@NotNull Player player, @NotNull Component nickname) {
        String cleanString = ComponentMessage.componentMessage(nickname).getAsPlainText();

        // Check if the player has admin perms
        if (player.hasPermission(NicknameModule.COMMAND_PERMISSION_ADMIN)) {
            return true;
        }

        // Check if the name is too long
        if (!player.hasPermission(NicknameModule.COMMAND_LENGTH_BYPASS_PERMISSION) && NicknameConfig.getInstance().isTooLong(cleanString)) {
            NicknameConfig.getInstance().getCommandTooLongMessage()
                .replace("{max-length}", String.valueOf(NicknameConfig.getInstance().getMaxLength()))
                .send(player);
            return false;

            // Check if the name is too short
        } else if (!player.hasPermission(NicknameModule.COMMAND_LENGTH_BYPASS_PERMISSION) && NicknameConfig.getInstance().isTooShort(cleanString)) {
            NicknameConfig.getInstance().getCommandTooShortMessage()
                .replace("{min-length}", String.valueOf(NicknameConfig.getInstance().getMinLength()))
                .send(player);
            return false;

            // Check if the name is blacklisted
        } else if (!player.hasPermission(NicknameModule.COMMAND_BLACKLIST_BYPASS_PERMISSION) && NicknameConfig.getInstance().isBlacklisted(cleanString)) {
            NicknameConfig.getInstance().getCommandBlacklistedMessage().send(player);
            return false;

            // Check if the name is unique
        } else if (!player.hasPermission(NicknameModule.COMMAND_UNIQUE_PERMISSION) && !cleanString.equalsIgnoreCase(player.getName())) {
            NicknameConfig.getInstance().getCommandNoUniqueMessage().send(player);
            return false;
        }
        return true;
    }

}
