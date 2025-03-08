package uk.firedev.firefly.modules.playtime.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.command.arguments.OfflinePlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.*;
import uk.firedev.firefly.modules.playtime.PlaytimeConfig;
import uk.firedev.firefly.modules.playtime.PlaytimeModule;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PlaytimeCommand {

    private static CommandTree command;

    // /playtime Command

    public static CommandTree getCommand() {
        if (command == null) {
            command = new CommandTree("playtime")
                    .withPermission("firefly.command.playtime")
                    .withShortDescription("Check Playtime")
                    .withFullDescription("Check Playtime")
                    .executesPlayer(info -> {
                        sendPlaytime(info.sender(), info.sender());
                    })
                    .then(
                        OfflinePlayerArgument.createPlayedBefore("target")
                            .executes((sender, arguments) -> {
                                OfflinePlayer target = Objects.requireNonNull(arguments.getUnchecked("target"));
                                sendPlaytime(sender, target);
                            })
                    )
                    .then(getSetPlaytimeBranch());
        }
        return command;
    }

    private static void sendPlaytime(@NotNull CommandSender sender, @NotNull OfflinePlayer playerToCheck) {
        ComponentReplacer replacer = ComponentReplacer.create(
                "player", Objects.requireNonNullElse(playerToCheck.getName(), "N/A"),
                "playtime", PlaytimeModule.getInstance().getTimeFormatted(playerToCheck)
        );
        PlaytimeConfig.getInstance().getCheckPlaytimeMessage()
            .replace("player", Objects.requireNonNull(playerToCheck.getName()))
            .replace("playtime", PlaytimeModule.getInstance().getTimeFormatted(playerToCheck))
            .applyReplacer(replacer).sendMessage(sender);
    }

    // /playtime set Branch

    private static Argument<String> getSetPlaytimeBranch() {
        return new LiteralArgument("set")
                .withPermission("firefly.command.playtime.admin")
                .thenNested(
                        OfflinePlayerArgument.createPlayedBefore("target"),
                        new LongArgument("playtime", 0)
                                .executes((sender, arguments) -> {
                                    OfflinePlayer target = Objects.requireNonNull(arguments.getUnchecked("target"));
                                    long newPlaytime = Objects.requireNonNull(arguments.getUnchecked("playtime"));
                                    PlaytimeModule.getInstance().setTime(target, newPlaytime);
                                    sendSetPlaytimeMessage(sender, target);
                                })
                );
    }

    private static void sendSetPlaytimeMessage(@NotNull CommandSender admin, @NotNull OfflinePlayer target) {
        Player player = target.getPlayer();
        String formattedTime = PlaytimeModule.getInstance().getTimeFormatted(target);
        if (player != null) {
            PlaytimeConfig.getInstance().getAdminSetPlaytimeMessage()
                    .replace("playtime", formattedTime)
                    .sendMessage(player);
        }
        if (admin != target) {
            PlaytimeConfig.getInstance().getAdminSetPlaytimeSenderMessage()
                    .replace("target", Objects.requireNonNullElse(target.getName(), "N/A"))
                    .replace("playtime", formattedTime)
                    .sendMessage(admin);
        }
    }

}
