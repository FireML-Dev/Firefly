package uk.firedev.firefly.modules.nickname.command;

import org.bukkit.OfflinePlayer;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.api.utils.PlayerHelper;
import uk.firedev.daisylib.command.ArgumentBuilder;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.nickname.NicknameConfig;
import uk.firedev.firefly.modules.nickname.NicknameModule;

public class NicknameCheckCommand {

    private static CommandAPICommand command = null;

    public static CommandAPICommand getCommand() {
        if (command == null) {
            command = new CommandAPICommand("nicknameCheck")
                    .withAliases("nickCheck")
                    .withArguments(NicknameCommand.getPlayersArgument().setOptional(true))
                    .withPermission(CommandPermission.fromString("firefly.command.nickname.check"))
                    .withShortDescription("Check a player's nickname")
                    .withFullDescription("Check a player's nickname")
                    .executesPlayer((player, arguments) -> {
                        String playerName = ArgumentBuilder.resolveArgumentOrThrow(arguments.get("player"), String.class);
                        OfflinePlayer target = PlayerHelper.getOfflinePlayer(playerName);
                        if (target == null) {
                            target = player;
                        }
                        String targetName = target.getName();
                        if (targetName == null) {
                            MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(player);
                            return;
                        }
                        ComponentReplacer replacer = ComponentReplacer.create()
                                .addReplacement("player", ComponentMessage.fromString(targetName).getMessage())
                                .addReplacement("nickname", NicknameModule.getInstance().getNickname(target));
                        NicknameConfig.getInstance().getCommandCheckInfoMessage().applyReplacer(replacer).sendMessage(player);
                    });
        }
        return command;
    }

}
