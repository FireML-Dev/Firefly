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
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.playtime.PlaytimeConfig;
import uk.firedev.firefly.modules.playtime.PlaytimeManager;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PlaytimeCommand extends CommandAPICommand {

    private static PlaytimeCommand instance;

    private PlaytimeCommand() {
        super("playtime");
        withArguments(getPlayersArgument(true));
        setPermission(CommandPermission.fromString("firefly.command.playtime"));
        withShortDescription("Check Playtime");
        withFullDescription("Check Playtime");
        withSubcommand(getSetPlaytimeSubCommand());
        executesPlayer((player, arguments) -> {
            Object argument = arguments.get("player");
            System.out.println(argument);
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
            ComponentReplacer replacer = new ComponentReplacer()
                    .addReplacement("player", Objects.requireNonNullElse(target.getName(), "N/A"))
                    .addReplacement("playtime", PlaytimeManager.getInstance().getTimeFormatted(target));
            PlaytimeConfig.getInstance().getCommandCheckPlaytimeMessage().applyReplacer(replacer).sendMessage(player);
        });
        executes((sender, arguments) -> {
            OfflinePlayer target = PlayerHelper.getOfflinePlayer(String.valueOf(arguments.get("player")));
            if (target == null) {
                MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(sender);
                return;
            }
            ComponentReplacer replacer = new ComponentReplacer()
                    .addReplacement("player", Objects.requireNonNullElse(target.getName(), "N/A"))
                    .addReplacement("playtime", PlaytimeManager.getInstance().getTimeFormatted(target));
            PlaytimeConfig.getInstance().getCommandCheckPlaytimeMessage().applyReplacer(replacer).sendMessage(sender);
        });
    }

    public static PlaytimeCommand getInstance() {
        if (instance == null) {
            instance = new PlaytimeCommand();
        }
        return instance;
    }

    private CommandAPICommand getSetPlaytimeSubCommand() {
        CommandAPICommand command = new CommandAPICommand("set-playtime")
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
                    PlaytimeManager.getInstance().setTime(target, playtime);
                    ComponentReplacer replacer = new ComponentReplacer()
                            .addReplacement("target", Objects.requireNonNullElse(target.getName(), "N/A"))
                            .addReplacement("playtime", PlaytimeManager.getInstance().getTimeFormatted(target));
                    PlaytimeConfig.getInstance().getCommandAdminSetPlaytimeMessage().applyReplacer(replacer).sendMessage(sender);
                });
        command.setPermission(CommandPermission.fromString("firefly.command.playtime.admin"));
        return command;
    }

    private Argument<?> getPlayersArgument(boolean optional) {
        return new StringArgument("player").setOptional(optional).includeSuggestions(ArgumentSuggestions.stringsAsync(info ->
                CompletableFuture.supplyAsync(() ->
                        Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)
                )
        ));
    }

}
