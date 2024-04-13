package uk.firedev.skylight.modules.nickname;

import net.kyori.adventure.text.Component;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.ArgumentSuggestions;
import uk.firedev.daisylib.libs.commandapi.arguments.GreedyStringArgument;
import uk.firedev.daisylib.utils.ComponentUtils;
import uk.firedev.skylight.config.MainConfig;
import uk.firedev.skylight.config.MessageConfig;
import uk.firedev.skylight.utils.StringUtils;

import java.util.Map;

public class NicknameCommand extends CommandAPICommand {

    private static NicknameCommand instance = null;

    private NicknameCommand() {
        super("nickname");
        withAliases("nick");
        withArguments(getNickArgument());
        setPermission(CommandPermission.fromString("skylight.command.nickname"));
        withShortDescription("Manage Nickname");
        withFullDescription("Manage Nickname");
        executesPlayer((player, arguments) -> {
            String[] args = arguments.rawArgs();
            Component currentNickname = NicknameManager.getInstance().getNickname(player);
            if (args.length == 0) {
                Component message = MessageConfig.getInstance().getConfig().getRichMessage("messages.nicknames.command.own-nickname");
                player.sendMessage(ComponentUtils.parsePlaceholders(message,
                        Map.of("nickname", currentNickname)
                ));
                return;
            }
            if (args[0].equals("remove") || args[0].equals("off")) {
                NicknameManager.getInstance().removeNickname(player);
                MessageConfig.getInstance().sendMessageFromConfig(player, "messages.nicknames.command.removed-nickname");
                return;
            }
            String[] splitValue = args[0].split(" ");
            Component nickname = StringUtils.convertLegacyToAdventure(splitValue[0]);
            String cleanString = ComponentUtils.toUncoloredString(nickname);
            boolean hasBypassLengthPermission = player.hasPermission("skylight.command.nickname.bypass.length");
            boolean hasBypassBlacklistPermission = player.hasPermission("skylight.command.nickname.bypass.blacklist");
            int maxLength = MainConfig.getInstance().getConfig().getInt("nicknames.max-length");
            int minLength = MainConfig.getInstance().getConfig().getInt("nicknames.min-length");
            if (cleanString.length() > maxLength && !hasBypassLengthPermission) {
                MessageConfig.getInstance().sendMessageFromConfig(player, "messages.nicknames.command.too-long", "max-length", String.valueOf(maxLength));
                return;
            } else if (cleanString.length() < minLength && !hasBypassLengthPermission) {
                MessageConfig.getInstance().sendMessageFromConfig(player, "messages.nicknames.command.too-short", "min-length", String.valueOf(minLength));
                return;
            } else if (MainConfig.getInstance().getConfig().getStringList("nicknames.blacklisted-names").contains(cleanString) && !hasBypassBlacklistPermission) {
                MessageConfig.getInstance().sendMessageFromConfig(player, "messages.nicknames.command.blacklisted");
                return;
            }
            NicknameManager.getInstance().setNickname(player, nickname);
            Component message = MessageConfig.getInstance().getConfig().getRichMessage("messages.nicknames.command.set-nickname");
            player.sendMessage(ComponentUtils.parsePlaceholders(message,
                    Map.of("nickname", nickname)
            ));
        });
    }

    public static NicknameCommand getInstance() {
        if (instance == null) {
            instance = new NicknameCommand();
        }
        return instance;
    }

    private Argument<?> getNickArgument() {
        return new GreedyStringArgument("value").setOptional(true).includeSuggestions(ArgumentSuggestions.strings("off", "remove"));
    }

}
