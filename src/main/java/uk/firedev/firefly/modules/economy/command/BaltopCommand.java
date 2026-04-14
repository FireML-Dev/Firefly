package uk.firedev.firefly.modules.economy.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.modules.economy.EconomyModule;
import uk.firedev.firefly.modules.economy.baltop.BaltopDialog;

import java.util.List;

public class BaltopCommand implements CommandHolder {

    /**
     * @return The command.
     */
    @Override
    public @NonNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("baltop")
            .requires(stack -> stack.getSender().hasPermission(permission()))
            .executes(ctx -> {
                Player player = CommandUtils.requirePlayer(ctx);
                BaltopDialog.open(player);
                return 1;
            })
            .build();
    }

    /**
     * @return The list of aliases this command should have.
     */
    @Override
    public @NonNull List<String> aliases() {
        return List.of();
    }

    /**
     * @return The permission for executing this command on yourself.
     */
    @Override
    public @NonNull String permission() {
        return EconomyModule.BALTOP_PERMISSION;
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @Override
    public @NonNull String targetPermission() {
        return permission();
    }

    /**
     * @return This command's description.
     */
    @Override
    public @Nullable String description() {
        return null;
    }

}
