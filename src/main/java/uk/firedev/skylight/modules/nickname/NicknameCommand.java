package uk.firedev.skylight.modules.nickname;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.ArgumentSuggestions;
import uk.firedev.daisylib.libs.commandapi.arguments.GreedyStringArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.daisylib.utils.ComponentUtils;
import uk.firedev.skylight.config.MainConfig;
import uk.firedev.skylight.config.MessageConfig;
import uk.firedev.skylight.utils.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
                String stringMessage = MessageConfig.getInstance().getConfig().getString("messages.nicknames.command.own-nickname", "<color:#F0E68C>Your Current Nickname is:</color> <white>{nickname}</white>");
                Component message = ComponentUtils.deserializeString(stringMessage);
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
            executeCommand(player, player, nickname);
        });
    }

    public static NicknameCommand getInstance() {
        if (instance == null) {
            instance = new NicknameCommand();
        }
        return instance;
    }

    public static Argument<?> getNickArgument() {
        return new GreedyStringArgument("value").setOptional(true).includeSuggestions(ArgumentSuggestions.strings("off", "remove"));
    }

    public static Argument<?> getPlayersArgument() {
        return new StringArgument("player").setOptional(false).includeSuggestions(ArgumentSuggestions.stringsAsync(info ->
                CompletableFuture.supplyAsync(() -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new))
        ));
    }

    public void executeCommand(@NotNull Player player, @NotNull OfflinePlayer target, @NotNull Component nickname) {
        // No color permission
        if (!player.hasPermission("skylight.command.nickname.colors")) {
            nickname = ComponentUtils.stripStyling(nickname);
        }
        final Component finalNickname = nickname;
        String cleanString = ComponentUtils.toPlainText(finalNickname);
        int maxLength = MainConfig.getInstance().getConfig().getInt("nicknames.max-length");
        int minLength = MainConfig.getInstance().getConfig().getInt("nicknames.min-length");
        String targetName = Objects.requireNonNullElse(target.getName(), "Error");
        // Too long and no bypass
        if (!player.hasPermission("skylight.command.nickname.bypass.length") && cleanString.length() > maxLength) {
            MessageConfig.getInstance().sendMessageFromConfig(player, "messages.nicknames.command.too-long", "max-length", String.valueOf(maxLength));
            return;
        // Too short and no bypass
        } else if (!player.hasPermission("skylight.command.nickname.bypass.length") && cleanString.length() < minLength) {
            MessageConfig.getInstance().sendMessageFromConfig(player, "messages.nicknames.command.too-short", "min-length", String.valueOf(minLength));
            return;
        // Blacklisted and no bypass
        } else if (!player.hasPermission("skylight.command.nickname.bypass.blacklist") && MainConfig.getInstance().getConfig().getStringList("nicknames.blacklisted-names").contains(cleanString)) {
            MessageConfig.getInstance().sendMessageFromConfig(player, "messages.nicknames.command.blacklisted");
            return;
        // Different name and no bypass
        } else if (!player.hasPermission("skylight.command.nickname.unique") && !ComponentUtils.matchesString(finalNickname, targetName)) {
            MessageConfig.getInstance().sendMessageFromConfig(player, "messages.nicknames.command.no-unique-name");
            return;
        }
        NicknameManager.getInstance().setNickname(target, finalNickname).thenRun(() -> {
            if (target.getPlayer() != null) {
                Component message = MessageConfig.getInstance().getConfig().getRichMessage("messages.nicknames.command.set-own-nickname");
                if (message != null) {
                    target.getPlayer().sendMessage(ComponentUtils.parsePlaceholders(message,
                            Map.of("nickname", finalNickname)
                    ));
                }
            }
            if (target.getUniqueId() != player.getUniqueId()) {
                Component message = MessageConfig.getInstance().getConfig().getRichMessage("messages.nicknames.command.admin.set-others-nickname");
                if (message != null) {
                    player.sendMessage(ComponentUtils.parsePlaceholders(message,
                            Map.of(
                                    "nickname", finalNickname,
                                    "target", ComponentUtils.deserializeString(targetName)
                            )
                    ));
                }
            }
        });
    }

}
