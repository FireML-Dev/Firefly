package uk.firedev.firefly.modules.teleportation.commands.back;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.argument.PlayerArgument;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;
import uk.firedev.firefly.modules.teleportation.TeleportModule;
import uk.firedev.firefly.utils.TeleportWarmup;

import java.util.List;

public class DBackCommand implements CommandHolder {

    @Override
    public @NonNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("dback")
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
    @NonNull
    @Override
    public List<String> aliases() {
        return List.of();
    }

    /**
     * @return The permission for executing this command on yourself.
     */
    @NonNull
    @Override
    public String permission() {
        return "firefly.command.dback";
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @NonNull
    @Override
    public String targetPermission() {
        return "firefly.command.dback.other";
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    public String description() {
        return null;
    }

    private static void teleportPlayer(@NonNull CommandSender sender, @NonNull Player target) {
        Location lastDeath = target.getLastDeathLocation();
        if (lastDeath == null) {
            TeleportConfig.getInstance().getLocationInvalidMessage().send(sender);
            return;
        }
        new TeleportWarmup(target, lastDeath, TeleportConfig.getInstance().getBackWarmupSeconds())
            .withSuccessMessage(() -> {
                if (sender instanceof Player player && player.equals(target)) {
                    return TeleportConfig.getInstance().getDBackTeleportedMessage();
                } else {
                    return TeleportConfig.getInstance().getDBackTeleportedSenderMessage()
                        .replace("{target}", target.getName());
                }
            })
            .start();
    }
    
}
