package uk.firedev.skylight.modules.nickname.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.skylight.config.MessageConfig;
import uk.firedev.skylight.modules.nickname.NicknameConfig;
import uk.firedev.skylight.modules.nickname.NicknameManager;

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
            OfflinePlayer target;
            try {
                target = PlayerHelper.getOfflinePlayer(args[0]);
                if (target == null) {
                    target = player;
                }
            } catch (IndexOutOfBoundsException ex) {
                target = player;
            }
            String targetName = target.getName();
            if (targetName == null) {
                MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(player);
                return;
            }
            ComponentReplacer replacer = new ComponentReplacer()
                    .addReplacement("player", new ComponentMessage(targetName).getMessage())
                    .addReplacement("nickname", NicknameManager.getInstance().getNickname(target));
            NicknameConfig.getInstance().getCommandCheckInfoMessage().applyReplacer(replacer).sendMessage(player);
        });
    }

    public static NicknameCheckCommand getInstance() {
        if (instance == null) {
            instance = new NicknameCheckCommand();
        }
        return instance;
    }

}
