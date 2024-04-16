package uk.firedev.skylight.modules.nickname;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.utils.ComponentUtils;
import uk.firedev.skylight.config.MessageConfig;

import java.util.Map;

public class NicknameCheckCommand extends CommandAPICommand {

    private static NicknameCheckCommand instance = null;

    private NicknameCheckCommand() {
        super("nicknamecheck");
        withAliases("nickcheck");
        withArguments(NicknameCommand.getPlayersArgument().setOptional(true));
        setPermission(CommandPermission.fromString("skylight.command.nickname.check"));
        withShortDescription("Check a player's nickname");
        withFullDescription("Check a player's nickname");
        executesPlayer((player, arguments) -> {
            String[] args = arguments.rawArgs();
            Component message = MessageConfig.getInstance().getConfig().getRichMessage("messages.nicknames.command.check.check-info");
            OfflinePlayer target;
            try {
                target = Bukkit.getOfflinePlayer(args[0]);
                if (!target.hasPlayedBefore() && !target.isOnline()) {
                    target = player;
                }
            } catch (IndexOutOfBoundsException ex) {
                target = player;
            }
            message = ComponentUtils.parsePlaceholders(message, Map.of(
                    "player", ComponentUtils.deserializeString(target.getName()),
                    "nickname", NicknameManager.getInstance().getNickname(target)
            ));
            player.sendMessage(message);
        });
    }

    public static NicknameCheckCommand getInstance() {
        if (instance == null) {
            instance = new NicknameCheckCommand();
        }
        return instance;
    }

}
