package uk.firedev.skylight.modules.nickname;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.ArgumentSuggestions;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.daisylib.utils.ComponentUtils;
import uk.firedev.skylight.config.MessageConfig;
import uk.firedev.skylight.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NicknameAdminCommand extends CommandAPICommand {

    private static NicknameAdminCommand instance = null;

    private NicknameAdminCommand() {
        super("nicknameadmin");
        withAliases("nickadmin");
        withArguments(NicknameCommand.getPlayersArgument(), NicknameCommand.getNickArgument());
        withPermission("skylight.command.nickname.admin");
        withShortDescription("Manage Nicknames as Admin");
        withFullDescription("Manage Nicknames as Admin");
        executesPlayer(((player, arguments) -> {
            String[] args = arguments.rawArgs();
            Component currentNickname = NicknameManager.getInstance().getNickname(player);
            if (args.length < 2) {
                Component message = MessageConfig.getInstance().getConfig().getRichMessage("messages.nicknames.command.admin-command.usage");
                player.sendMessage(ComponentUtils.parsePlaceholders(message,
                        Map.of("nickname", currentNickname)
                ));
                return;
            }
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);
            if (!targetPlayer.hasPlayedBefore()) {
                MessageConfig.getInstance().sendMessageFromConfig(player, "messages.player-not-found");
                return;
            }
            if (args[1].equals("remove") || args[1].equals("off")) {
                NicknameManager.getInstance().removeNickname(targetPlayer);
                MessageConfig.getInstance().sendMessageFromConfig(player, "messages.nicknames.command.admin.removed-others-nickname",
                        "player", targetPlayer.getName()
                );
                if (targetPlayer.getUniqueId() != player.getUniqueId() && targetPlayer.getPlayer() != null) {
                    MessageConfig.getInstance().sendMessageFromConfig(targetPlayer.getPlayer(), "messages.nicknames.command.removed-nickname");
                }
                return;
            }
            String[] splitValue = args[1].split(" ");
            Component nickname = StringUtils.convertLegacyToAdventure(splitValue[0]);
            NicknameCommand.getInstance().executeCommand(player, targetPlayer, nickname);
        }));
    }

    public static NicknameAdminCommand getInstance() {
        if (instance == null) {
            instance = new NicknameAdminCommand();
        }
        return instance;
    }

}
