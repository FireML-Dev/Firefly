package uk.firedev.firefly.modules.playtime.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.ArgumentBase;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.OfflinePlayerArgument;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.modules.playtime.PlaytimeConfig;
import uk.firedev.firefly.modules.playtime.PlaytimeModule;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;

import java.util.Map;
import java.util.Objects;

public class PlaytimeCommand {

    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("playtime")
            .requires(stack -> PlaytimeModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission("firefly.command.playtime"))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                sendPlaytime(player, player);
                return 1;
            })
            .then(set())
            .then(check())
            .build();
    }

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set")
            .requires(stack -> stack.getSender().hasPermission("firefly.command.playtime.set"))
            .then(
                Commands.argument("target", OfflinePlayerArgument.create(PlayerHelper::hasPlayerBeenOnServer))
                    .then(
                        Commands.argument("playtime", LongArgumentType.longArg(0))
                            .executes(context -> {
                                OfflinePlayer target = context.getArgument("target", OfflinePlayer.class);
                                long newPlaytime = context.getArgument("playtime", long.class);
                                PlaytimeModule.getInstance().setTime(target, newPlaytime);
                                sendSetPlaytimeMessage(context.getSource().getSender(), target);
                                return 1;
                            })
                    )
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> check() {
        return Commands.literal("check")
            .then(
                Commands.argument("target", OfflinePlayerArgument.create(PlayerHelper::hasPlayerBeenOnServer))
                    .executes(context -> {
                        OfflinePlayer target = context.getArgument("target", OfflinePlayer.class);
                        sendPlaytime(context.getSource().getSender(), target);
                        return 1;
                    })
            );
    }

    // Convenience

    private void sendPlaytime(@NotNull CommandSender sender, @NotNull OfflinePlayer playerToCheck) {
        Replacer replacer = Replacer.replacer().addReplacements(Map.of(
            "{player}", Objects.requireNonNullElse(playerToCheck.getName(), "N/A"),
            "{playtime}", PlaytimeModule.getInstance().getTimeFormatted(playerToCheck)
        ));
        PlaytimeConfig.getInstance().getCheckPlaytimeMessage().replace(replacer).send(sender);
    }

    private void sendSetPlaytimeMessage(@NotNull CommandSender admin, @NotNull OfflinePlayer target) {
        Player player = target.getPlayer();
        String formattedTime = PlaytimeModule.getInstance().getTimeFormatted(target);
        if (player != null) {
            PlaytimeConfig.getInstance().getAdminSetPlaytimeMessage()
                .replace("{playtime}", formattedTime)
                .send(player);
        }
        if (admin != target) {
            PlaytimeConfig.getInstance().getAdminSetPlaytimeSenderMessage()
                .replace("{target}", Objects.requireNonNullElse(target.getName(), "N/A"))
                .replace("{playtime}", formattedTime)
                .send(admin);
        }
    }

}
