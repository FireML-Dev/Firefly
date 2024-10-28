package uk.firedev.firefly.modules.nickname.command;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.nickname.NicknameConfig;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.utils.StringUtils;

public class NicknameAdminCommand extends CommandAPICommand {

    private static NicknameAdminCommand instance = null;

    private NicknameAdminCommand() {
        super("nicknameadmin");
        withAliases("nickadmin");
        withArguments(NicknameCommand.getPlayersArgument(), NicknameCommand.getNickArgument());
        withPermission("firefly.command.nickname.admin");
        withShortDescription("Manage Nicknames as Admin");
        withFullDescription("Manage Nicknames as Admin");
        executesPlayer(((player, arguments) -> {
            String[] args = arguments.rawArgs();
            Component currentNickname = NicknameModule.getInstance().getNickname(player);
            if (args.length < 2) {
                NicknameConfig.getInstance().getCommandAdminUsageMessage().sendMessage(player);
                return;
            }
            OfflinePlayer targetPlayer = PlayerHelper.getOfflinePlayer(args[0]);
            if (targetPlayer == null) {
                MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(player);
                return;
            }
            String targetName = targetPlayer.getName();
            if (targetName == null) {
                MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(player);
                return;
            }
            if (args[1].equals("remove") || args[1].equals("off")) {
                NicknameModule.getInstance().removeNickname(targetPlayer);
                ComponentReplacer replacer = ComponentReplacer.componentReplacer("player", targetName);
                NicknameConfig.getInstance().getCommandAdminRemovedNicknameMessage().applyReplacer(replacer).sendMessage(player);
                if (targetPlayer.getUniqueId() != player.getUniqueId() && targetPlayer.getPlayer() != null) {
                    NicknameConfig.getInstance().getCommandRemovedNicknameMessage().sendMessage(targetPlayer.getPlayer());
                }
                return;
            }
            String[] splitValue = args[1].split(" ");
            Component nickname = StringUtils.getColorOnlyComponent(splitValue[0]);
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
