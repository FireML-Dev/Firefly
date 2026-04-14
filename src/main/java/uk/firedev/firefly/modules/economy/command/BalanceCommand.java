package uk.firedev.firefly.modules.economy.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.argument.OfflinePlayerArgument;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.modules.economy.EconomyConfig;
import uk.firedev.firefly.modules.economy.EconomyModule;

import java.util.List;

public class BalanceCommand implements CommandHolder {

    @Override
    public @NonNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("balance")
            .requires(stack -> stack.getSender().hasPermission(permission()))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                checkBalance(player, player);
                return 1;
            })
            .then(
                Commands.argument("target", OfflinePlayerArgument.create())
                    .requires(stack -> stack.getSender().hasPermission(targetPermission()))
                    .executes(ctx -> {
                        OfflinePlayer target = ctx.getArgument("target", OfflinePlayer.class);
                        checkBalance(ctx.getSource().getSender(), target);
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
        return List.of("bal");
    }

    /**
     * @return The permission for executing this command on yourself.
     */
    @NonNull
    @Override
    public String permission() {
        return EconomyModule.BALANCE_PERMISSION;
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @NonNull
    @Override
    public String targetPermission() {
        return permission();
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    public String description() {
        return null;
    }

    // Convenience

    protected static void checkBalance(@NonNull CommandSender sender, @NonNull OfflinePlayer target) {
        EconomyConfig.getInstance().getBalanceMessage(target).send(sender);
    }

}
