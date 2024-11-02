package uk.firedev.firefly.modules.nickname.command;

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
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.nickname.NicknameConfig;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.utils.StringUtils;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class NicknameCommand extends CommandAPICommand {

    private static NicknameCommand instance = null;

    private NicknameCommand() {
        super("nickname");
        withAliases("nick");
        withArguments(getNickArgument());
        setPermission(CommandPermission.fromString("firefly.command.nickname"));
        withShortDescription("Manage Nickname");
        withFullDescription("Manage Nickname");
        executesPlayer((player, arguments) -> {
            String[] args = arguments.rawArgs();
            Component currentNickname = NicknameModule.getInstance().getNickname(player);
            if (args.length == 0) {
                ComponentReplacer replacer = ComponentReplacer.componentReplacer()
                        .addReplacement("nickname", currentNickname)
                        .addReplacement("player", player.getName());
                NicknameConfig.getInstance().getCommandCheckInfoMessage().applyReplacer(replacer).sendMessage(player);
                return;
            }
            if (args[0].equals("remove") || args[0].equals("off")) {
                NicknameModule.getInstance().removeNickname(player);
                NicknameConfig.getInstance().getCommandRemovedNicknameMessage().sendMessage(player);
                return;
            }
            String[] splitValue = args[0].split(" ");
            Component nickname = StringUtils.getColorOnlyComponent(splitValue[0]);
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
        ComponentMessage nicknameMessage = ComponentMessage.of(nickname);
        if (!player.hasPermission("firefly.command.nickname.colors")) {
            nicknameMessage = nicknameMessage.stripStyling();
        }
        final ComponentMessage finalNicknameMessage = nicknameMessage;
        String cleanString = finalNicknameMessage.toPlainText();
        String targetName = Objects.requireNonNullElse(target.getName(), "Error");
        // Too long and no bypass
        if (!player.hasPermission("firefly.command.nickname.bypass.length") && cleanString.length() > NicknameConfig.getInstance().getMaxLength()) {
            ComponentReplacer replacer = ComponentReplacer.componentReplacer("max-length", String.valueOf(NicknameConfig.getInstance().getMaxLength()));
            NicknameConfig.getInstance().getCommandTooLongMessage().applyReplacer(replacer).sendMessage(player);
            return;
        // Too short and no bypass
        } else if (!player.hasPermission("firefly.command.nickname.bypass.length") && cleanString.length() < NicknameConfig.getInstance().getMinLength()) {
            ComponentReplacer replacer = ComponentReplacer.componentReplacer("min-length", String.valueOf(NicknameConfig.getInstance().getMinLength()));
            NicknameConfig.getInstance().getCommandTooShortMessage().applyReplacer(replacer).sendMessage(player);
            return;
        // Blacklisted and no bypass
        } else if (!player.hasPermission("firefly.command.nickname.bypass.blacklist") && NicknameConfig.getInstance().getBlacklistedNames().contains(cleanString)) {
            NicknameConfig.getInstance().getCommandBlacklistedMessage().sendMessage(player);
            return;
        // Different name and no bypass
        } else if (!player.hasPermission("firefly.command.nickname.unique") && !finalNicknameMessage.matchesString(targetName)) {
            NicknameConfig.getInstance().getCommandNoUniqueMessage().sendMessage(player);
            return;
        }
        if (NicknameModule.getInstance().setNickname(target, finalNicknameMessage.getMessage())) {
            ComponentReplacer replacer = ComponentReplacer.componentReplacer("nickname", finalNicknameMessage.getMessage());
            if (target.getPlayer() != null) {
                NicknameConfig.getInstance().getCommandSetOwnNicknameMessage().applyReplacer(replacer).sendMessage(target.getPlayer());
            }
            if (target.getUniqueId() != player.getUniqueId()) {
                replacer.addReplacements("target", targetName);
                NicknameConfig.getInstance().getCommandAdminSetNicknameMessage().applyReplacer(replacer).sendMessage(player);
            }
        } else {
            MessageConfig.getInstance().getErrorOccurredMessage().sendMessage(player);
        }
    }

}
