package uk.firedev.firefly.modules.playtime.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.ArgumentSuggestions;
import uk.firedev.daisylib.libs.commandapi.arguments.LongArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.daisylib.libs.commandapi.executors.CommandExecutor;
import uk.firedev.daisylib.libs.commandapi.executors.PlayerCommandExecutor;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.playtime.PlaytimeConfig;
import uk.firedev.firefly.modules.playtime.PlaytimeModule;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PlaytimeCommand {

    private static CommandAPICommand command;

    // /playtime Command

    public static CommandAPICommand getCommand() {
        if (command == null) {
            command = new CommandAPICommand("playtime")
                    .withArguments(getPlayersArgument(true))
                    .withPermission(CommandPermission.fromString("firefly.command.playtime"))
                    .withShortDescription("Check Playtime")
                    .withFullDescription("Check Playtime")
                    .withSubcommand(getSetPlaytimeSubCommand())
                    .executesPlayer(getPlayerExecutor())
                    .executes(getOtherExecutor());
        }
        return command;
    }

    private static PlayerCommandExecutor getPlayerExecutor() {
        return (player, arguments) -> {
            Object argument = arguments.get("player");
            OfflinePlayer target;
            if (argument == null) {
                target = player;
            } else {
                target = PlayerHelper.getOfflinePlayer(String.valueOf(argument));
                if (target == null) {
                    MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(player);
                    return;
                }
            }
            ComponentReplacer replacer = ComponentReplacer.componentReplacer(
                    "player", Objects.requireNonNullElse(target.getName(), "N/A"),
                    "playtime", PlaytimeModule.getInstance().getTimeFormatted(target)
            );
            PlaytimeConfig.getInstance().getCommandCheckPlaytimeMessage().applyReplacer(replacer).sendMessage(player);
        };
    }

    private static CommandExecutor getOtherExecutor() {
        return (sender, arguments) -> {
            OfflinePlayer target = PlayerHelper.getOfflinePlayer(String.valueOf(arguments.get("player")));
            if (target == null) {
                MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(sender);
                return;
            }
            ComponentReplacer replacer = ComponentReplacer.componentReplacer(
                    "player", Objects.requireNonNullElse(target.getName(), "N/A"),
                    "playtime", PlaytimeModule.getInstance().getTimeFormatted(target)
            );
            PlaytimeConfig.getInstance().getCommandCheckPlaytimeMessage().applyReplacer(replacer).sendMessage(sender);
        };
    }

    // /playtime set Command

    private static CommandAPICommand getSetPlaytimeSubCommand() {
        CommandAPICommand command = new CommandAPICommand("set")
                .withArguments(getPlayersArgument(false), new LongArgument("playtime"))
                .withFullDescription("Set a player's playtime")
                .withShortDescription("Set a player's playtime")
                .executes((sender, arguments) -> {
                    OfflinePlayer target = PlayerHelper.getOfflinePlayer(String.valueOf(arguments.get("player")));
                    long playtime = (long) Objects.requireNonNull(arguments.get("playtime"));
                    if (target == null) {
                        MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(sender);
                        return;
                    }
                    PlaytimeModule.getInstance().setTime(target, playtime);
                    ComponentReplacer replacer = ComponentReplacer.componentReplacer(
                            "target", Objects.requireNonNullElse(target.getName(), "N/A"),
                            "playtime", PlaytimeModule.getInstance().getTimeFormatted(target)
                    );
                    PlaytimeConfig.getInstance().getCommandAdminSetPlaytimeMessage().applyReplacer(replacer).sendMessage(sender);
                });
        command.setPermission(CommandPermission.fromString("firefly.command.playtime.admin"));
        return command;
    }

    // Argument stuff

    private static Argument<?> getPlayersArgument(boolean optional) {
        return new StringArgument("player").setOptional(optional).includeSuggestions(ArgumentSuggestions.stringsAsync(info ->
                CompletableFuture.supplyAsync(() ->
                        Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)
                )
        ));
    }

}
