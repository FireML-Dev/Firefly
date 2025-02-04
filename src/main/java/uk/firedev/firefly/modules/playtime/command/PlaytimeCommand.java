package uk.firedev.firefly.modules.playtime.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.AsyncOfflinePlayerArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.daisylib.libs.commandapi.arguments.LongArgument;
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
                    .withPermission(CommandPermission.fromString("firefly.command.playtime"))
                    .withShortDescription("Check Playtime")
                    .withFullDescription("Check Playtime")
                    .executesPlayer(info -> {
                        sendPlaytime(info.sender(), info.sender());
                    })
                    .then(
                        new AsyncOfflinePlayerArgument("target")
                            .executes((sender, arguments) -> {
                                CompletableFuture<OfflinePlayer> target = Objects.requireNonNull(arguments.getUnchecked("target"));
                                target.thenAccept(offlinePlayer -> sendPlaytime(sender, offlinePlayer));
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
        PlaytimeConfig.getInstance().getCheckPlaytimeMessage().applyReplacer(replacer).sendMessage(sender);
    }

    // /playtime set Branch

    private static Argument<String> getSetPlaytimeBranch() {
        return new LiteralArgument("set")
                .withPermission("firefly.command.playtime.admin")
                .thenNested(
                        new AsyncOfflinePlayerArgument("target"),
                        new LongArgument("playtime", 0)
                                .executes((sender, arguments) -> {
                                    CompletableFuture<OfflinePlayer> future = Objects.requireNonNull(arguments.getUnchecked("target"));
                                    long newPlaytime = Objects.requireNonNull(arguments.getUnchecked("playtime"));
                                    future.thenAccept(target -> {
                                        PlaytimeModule.getInstance().setTime(target, newPlaytime);
                                        sendSetPlaytimeMessage(sender, target);
                                    });
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
