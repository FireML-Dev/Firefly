package uk.firedev.firefly.modules.nickname.command;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import uk.firedev.daisylib.command.ArgumentBuilder;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.api.utils.PlayerHelper;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.nickname.NicknameConfig;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.utils.StringUtils;

public class NicknameAdminCommand {

    private static CommandAPICommand command = null;

    public static CommandAPICommand getCommand() {
        if (command == null) {
            command = new CommandAPICommand("nicknameAdmin")
                    .withAliases("nickAdmin")
                    .withPermission("firefly.command.nickname.admin")
                    .withShortDescription("Manage Nicknames as Admin")
                    .withFullDescription("Manage Nicknames as Admin")
                    .withArguments(NicknameCommand.getPlayersArgument(), NicknameCommand.getNickArgument())
                    .executesPlayer(((player, arguments) -> {
                        if (arguments.count() < 2) {
                            NicknameConfig.getInstance().getCommandAdminUsageMessage().sendMessage(player);
                            return;
                        }
                        String playerName = ArgumentBuilder.resolveArgumentOrThrow(arguments.get("player"), String.class);
                        String nicknameStr = ArgumentBuilder.resolveArgumentOrThrow(arguments.get("value"), String.class);
                        OfflinePlayer targetPlayer = PlayerHelper.getOfflinePlayer(playerName);
                        if (targetPlayer == null) {
                            MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(player);
                            return;
                        }
                        String targetName = targetPlayer.getName();
                        if (targetName == null) {
                            MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(player);
                            return;
                        }
                        if (nicknameStr.equalsIgnoreCase("remove") || nicknameStr.equalsIgnoreCase("off")) {
                            NicknameModule.getInstance().removeNickname(targetPlayer);
                            ComponentReplacer replacer = ComponentReplacer.componentReplacer("player", targetName);
                            NicknameConfig.getInstance().getCommandAdminRemovedNicknameMessage().applyReplacer(replacer).sendMessage(player);
                            if (targetPlayer.getUniqueId() != player.getUniqueId() && targetPlayer.getPlayer() != null) {
                                NicknameConfig.getInstance().getCommandRemovedNicknameMessage().sendMessage(targetPlayer.getPlayer());
                            }
                            return;
                        }
                        String[] splitValue = nicknameStr.split(" ");
                        Component nickname = StringUtils.getColorOnlyComponent(splitValue[0]);
                        NicknameCommand.executeCommand(player, targetPlayer, nickname);
                    }));
        }
        return command;
    }

}
