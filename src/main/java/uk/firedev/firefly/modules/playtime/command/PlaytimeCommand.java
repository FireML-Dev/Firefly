package uk.firedev.firefly.modules.playtime.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.OfflinePlayerArgument;
import uk.firedev.daisylib.utils.PlayerHelper;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.modules.playtime.PlaytimeConfig;
import uk.firedev.firefly.modules.playtime.PlaytimeModule;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlaytimeCommand implements CommandHolder {

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("playtime")
            .requires(stack -> PlaytimeModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission(permission()))
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

    /**
     * @return The list of aliases this command should have.
     */
    @NotNull
    @Override
    public List<String> aliases() {
        return List.of();
    }

    /**
     * @return The permission for executing this command on yourself.
     */
    @NotNull
    @Override
    public String permission() {
        return "firefly.command.playtime";
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @NotNull
    @Override
    public String targetPermission() {
        return "firefly.command.playtime";
    }

    public String setPermission() {
        return "firefly.command.playtime.set";
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    public String description() {
        return null;
    }

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set")
            .requires(stack -> stack.getSender().hasPermission(setPermission()))
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
