package uk.firedev.firefly.modules.nickname.command;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.arguments.OfflinePlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.GreedyStringArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.firefly.modules.nickname.NicknameConfig;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.utils.StringUtils;
import uk.firedev.messagelib.message.ComponentMessage;

import java.util.Objects;

public class NicknameCommand {

    public static CommandTree getCommand() {
        return new CommandTree("nickname")
            .withAliases("nick")
            .withPermission(NicknameModule.COMMAND_PERMISSION)
            .withFullDescription("Manage Nickname")
            .executesPlayer(info -> {
                checkNickname(info.sender(), info.sender());
            })
            .then(getCheckBranch())
            .then(getSetBranch())
            .then(getSetOthersBranch())
            .then(getRemoveBranch());
    }

    private static Argument<String> getCheckBranch() {
        return new LiteralArgument("check")
            .executesPlayer(info -> {
                checkNickname(info.sender(), info.sender());
            })
            .then(
                OfflinePlayerArgument.createPlayedBefore("target")
                    .executes(info -> {
                        OfflinePlayer target = Objects.requireNonNull(info.args().getUnchecked("target"));
                        checkNickname(info.sender(), target);
                    })
            );
    }

    private static void checkNickname(@NotNull CommandSender sender, @NotNull OfflinePlayer target) {
        Component nickname = NicknameModule.getInstance().getNickname(target);
        NicknameConfig.getInstance().getCommandCheckInfoMessage()
            .replace("nickname", nickname)
            .replace("player", Objects.requireNonNullElse(target.getName(), "N/A"))
            .send(sender);
    }

    private static Argument<String> getRemoveBranch() {
        return new LiteralArgument("remove")
            .executesPlayer(info -> {
                NicknameModule.getInstance().removeNickname(info.sender());
                NicknameConfig.getInstance().getCommandRemovedNicknameMessage().send(info.sender());
            })
            .then(
                OfflinePlayerArgument.createPlayedBefore("target")
                    .withPermission("firefly")
                    .executes(info -> {
                        OfflinePlayer target = Objects.requireNonNull(info.args().getUnchecked("target"));

                        // Remove nickname
                        NicknameModule.getInstance().removeNickname(target);

                        // If target is online, tell them
                        Player onlineTarget = target.getPlayer();
                        if (onlineTarget != null) {
                            NicknameConfig.getInstance().getCommandRemovedNicknameMessage().send(onlineTarget);
                        }

                        // If target isn't the sender, tell them
                        if (target != info.sender()) {
                            NicknameConfig.getInstance().getCommandAdminRemovedNicknameMessage().send(info.sender());
                        }
                    })
            );
    }

    private static Argument<String> getSetBranch() {
        return new LiteralArgument("set")
            .then(
                new GreedyStringArgument("nickname")
                    .executesPlayer(info -> {
                        String nickname = Objects.requireNonNull(info.args().getUnchecked("nickname"));
                        nickname = nickname.split(" ")[0]; // Only use the name before the first space
                        Component componentNickname = StringUtils.getColorOnlyComponent(nickname);
                        if (!validateNickname(info.sender(), componentNickname)) {
                            return;
                        }
                        NicknameModule.getInstance().setNickname(info.sender(), nickname);
                        NicknameConfig.getInstance().getCommandSetOwnNicknameMessage()
                            .replace("nickname", componentNickname)
                            .send(info.sender());
                    })
            );
    }

    private static Argument<String> getSetOthersBranch() {
        return new LiteralArgument("setOther")
            .withPermission("firefly.command.nickname.other")
            .thenNested(
                OfflinePlayerArgument.createPlayedBefore("target"),
                new GreedyStringArgument("nickname")
                    .executes(info -> {
                        OfflinePlayer target = Objects.requireNonNull(info.args().getUnchecked("target"));

                        String nickname = Objects.requireNonNull(info.args().getUnchecked("nickname"));
                        nickname = nickname.split(" ")[0]; // Only use the name before the first space
                        Component componentNickname = StringUtils.getColorOnlyComponent(nickname);
                        NicknameModule.getInstance().setNickname(target, componentNickname);

                        // If target is online, tell them
                        Player onlineTarget = target.getPlayer();
                        if (onlineTarget != null) {
                            NicknameConfig.getInstance().getCommandSetOwnNicknameMessage()
                                .replace("nickname", componentNickname)
                                .send(onlineTarget);
                        }

                        // If target isn't the sender, tell them
                        if (target != info.sender()) {
                            NicknameConfig.getInstance().getCommandAdminSetNicknameMessage()
                                    .replace("target", Objects.requireNonNull(target.getName()))
                                    .replace("nickname", componentNickname)
                                    .send(info.sender());
                        }
                    })
            );
    }

    private static boolean validateNickname(@NotNull Player player, @NotNull Component nickname) {
        String cleanString = ComponentMessage.componentMessage(nickname).getAsPlainText();

        // Check if the player has admin perms
        if (player.hasPermission(NicknameModule.COMMAND_PERMISSION_ADMIN)) {
            return true;
        }

        // Check if the name is too long
        if (!player.hasPermission(NicknameModule.COMMAND_LENGTH_BYPASS_PERMISSION) && NicknameConfig.getInstance().isTooLong(cleanString)) {
            NicknameConfig.getInstance().getCommandTooLongMessage()
                .replace("max-length", String.valueOf(NicknameConfig.getInstance().getMaxLength()))
                .send(player);
            return false;

        // Check if the name is too short
        } else if (!player.hasPermission(NicknameModule.COMMAND_LENGTH_BYPASS_PERMISSION) && NicknameConfig.getInstance().isTooShort(cleanString)) {
            NicknameConfig.getInstance().getCommandTooShortMessage()
                .replace("min-length", String.valueOf(NicknameConfig.getInstance().getMinLength()))
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
