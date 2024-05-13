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

import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.message.component.ComponentReplacer;
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
                ComponentReplacer replacer = new ComponentReplacer()
                        .addReplacement("nickname", currentNickname)
                        .addReplacement("player", player.getName());
                NicknameConfig.getInstance().getCommandCheckInfoMessage().applyReplacer(replacer).sendMessage(player);
                return;
            }
            if (args[0].equals("remove") || args[0].equals("off")) {
                NicknameManager.getInstance().removeNickname(player);
                NicknameConfig.getInstance().getCommandRemovedNicknameMessage().sendMessage(player);
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
        ComponentMessage nicknameMessage = new ComponentMessage(nickname);
        if (!player.hasPermission("skylight.command.nickname.colors")) {
            nicknameMessage = nicknameMessage.stripStyling();
        }
        final ComponentMessage finalNicknameMessage = nicknameMessage;
        String cleanString = finalNicknameMessage.toPlainText();
        String targetName = Objects.requireNonNullElse(target.getName(), "Error");
        // Too long and no bypass
        if (!player.hasPermission("skylight.command.nickname.bypass.length") && cleanString.length() > NicknameConfig.getInstance().getMaxLength()) {
            ComponentReplacer replacer = new ComponentReplacer().addReplacements("max-length", String.valueOf(NicknameConfig.getInstance().getMaxLength()));
            NicknameConfig.getInstance().getCommandTooLongMessage().applyReplacer(replacer).sendMessage(player);
            return;
        // Too short and no bypass
        } else if (!player.hasPermission("skylight.command.nickname.bypass.length") && cleanString.length() < NicknameConfig.getInstance().getMinLength()) {
            ComponentReplacer replacer = new ComponentReplacer().addReplacements("min-length", String.valueOf(NicknameConfig.getInstance().getMinLength()));
            NicknameConfig.getInstance().getCommandTooShortMessage().applyReplacer(replacer).sendMessage(player);
            return;
        // Blacklisted and no bypass
        } else if (!player.hasPermission("skylight.command.nickname.bypass.blacklist") && NicknameConfig.getInstance().getBlacklistedNames().contains(cleanString)) {
            NicknameConfig.getInstance().getCommandBlacklistedMessage().sendMessage(player);
            return;
        // Different name and no bypass
        } else if (!player.hasPermission("skylight.command.nickname.unique") && !finalNicknameMessage.matchesString(targetName)) {
            NicknameConfig.getInstance().getCommandNoUniqueMessage().sendMessage(player);
            return;
        }
        NicknameManager.getInstance().setNickname(target, finalNicknameMessage.getMessage()).thenRun(() -> {
            ComponentReplacer replacer = new ComponentReplacer().addReplacement("nickname", finalNicknameMessage.getMessage());
            if (target.getPlayer() != null) {
                NicknameConfig.getInstance().getCommandSetOwnNicknameMessage().applyReplacer(replacer).sendMessage(target.getPlayer());
            }
            if (target.getUniqueId() != player.getUniqueId()) {
                replacer.addReplacements("target", targetName);
                NicknameConfig.getInstance().getCommandAdminSetNicknameMessage().applyReplacer(replacer).sendMessage(player);
            }
        });
    }

}
