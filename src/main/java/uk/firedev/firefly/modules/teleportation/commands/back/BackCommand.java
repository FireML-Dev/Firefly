package uk.firedev.firefly.modules.teleportation.commands.back;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;
import uk.firedev.firefly.modules.teleportation.TeleportModule;
import uk.firedev.firefly.utils.TeleportWarmup;

import java.util.List;

public class BackCommand implements CommandHolder {

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("back")
            .requires(stack -> TeleportModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission(permission()))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                teleportPlayer(player, player);
                return 1;
            })
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .requires(stack -> stack.getSender().hasPermission(targetPermission()))
                    .executes(context -> {
                        Player target = context.getArgument("target", Player.class);
                        teleportPlayer(context.getSource().getSender(), target);
                        return 1;
                    })
            )
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
        return "firefly.command.back";
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @NotNull
    @Override
    public String targetPermission() {
        return "firefly.command.back.other";
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    public String description() {
        return null;
    }

    private void teleportPlayer(@NotNull CommandSender sender, @NotNull Player target) {
        Location location = TeleportModule.getInstance().getLastLocation(target);
        if (location == null) {
            TeleportConfig.getInstance().getLocationInvalidMessage().send(sender);
            return;
        }
        new TeleportWarmup(target, location, TeleportConfig.getInstance().getBackWarmupSeconds())
            .withSuccessMessage(() -> {
                if (sender instanceof Player player && player.equals(target)) {
                    return TeleportConfig.getInstance().getBackTeleportedMessage();
                } else {
                    return TeleportConfig.getInstance().getBackTeleportedSenderMessage()
                        .replace("{target}", target.getName());
                    }
            })
            .start();
    }

}
