package uk.firedev.firefly.modules.nickname.command;

import org.bukkit.OfflinePlayer;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.nickname.NicknameConfig;
import uk.firedev.firefly.modules.nickname.NicknameManager;

public class NicknameCheckCommand extends CommandAPICommand {

    private static NicknameCheckCommand instance = null;

    private NicknameCheckCommand() {
        super("nicknamecheck");
        withAliases("nickcheck");
        withArguments(NicknameCommand.getPlayersArgument().setOptional(true));
        setPermission(CommandPermission.fromString("firefly.command.nickname.check"));
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
                    .addReplacement("player", ComponentMessage.fromString(targetName).getMessage())
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
