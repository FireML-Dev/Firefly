package uk.firedev.firefly.modules.teleportation.commands.tpa;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.modules.teleportation.TeleportModule;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;
import uk.firedev.firefly.modules.teleportation.tpa.TPARequest;

import java.util.List;

public class TPACommand implements CommandHolder {

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("tpa")
            .requires(stack -> TeleportModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission(permission()))
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .executes(context -> {
                        Player player = CommandUtils.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        Player target = context.getArgument("target", Player.class);
                        TPAHandler.getInstance().sendRequest(target, player, TPARequest.TPADirection.SENDER_TO_TARGET);
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
        return "firefly.command.tpa";
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @NotNull
    @Override
    public String targetPermission() {
        return "firefly.command.tpa";
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    public String description() {
        return null;
    }

}
