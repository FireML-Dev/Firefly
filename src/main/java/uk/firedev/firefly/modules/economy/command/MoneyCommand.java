package uk.firedev.firefly.modules.economy.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
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
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.economy.EconomyConfig;
import uk.firedev.firefly.modules.economy.EconomyModule;

import java.util.List;

public class MoneyCommand implements CommandHolder {

    /**
     * @return The command.
     */
    @Override
    public @NonNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("money")
            .requires(stack -> stack.getSender().hasPermission(permission()))
            .then(
                Commands.argument("player", OfflinePlayerArgument.create())
                    .then(set())
                    .then(check())
                    .then(add())
                    .then(take())
                    .then(transfer())
            ).build();
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
        return EconomyModule.MONEY_PERMISSION;
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

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        return Commands.literal("set")
            .then(
                Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                    .executes(ctx -> {
                        OfflinePlayer player = ctx.getArgument("player", OfflinePlayer.class);
                        double amount = ctx.getArgument("amount", double.class);

                        PlayerData data = PlayerData.playerData(player.getUniqueId());
                        data.setBalance(amount);

                        ComponentMessage.componentMessage("Set {player}'s balance to {amount}.")
                            .replace("{player}", data.getNickname())
                            .replace("{amount}", EconomyConfig.getInstance().format(amount))
                            .send(ctx.getSource().getSender());
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> check() {
        return Commands.literal("check")
            .executes(ctx -> {
                OfflinePlayer player = ctx.getArgument("player", OfflinePlayer.class);
                BalanceCommand.checkBalance(ctx.getSource().getSender(), player);
                return 1;
            });
    }

    private ArgumentBuilder<CommandSourceStack, ?> add() {
        return Commands.literal("add")
            .then(
                Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                    .executes(ctx -> {
                        OfflinePlayer player = ctx.getArgument("player", OfflinePlayer.class);
                        double amount = ctx.getArgument("amount", double.class);

                        PlayerData data = PlayerData.playerData(player.getUniqueId());
                        data.incrementBalance(amount);

                        ComponentMessage.componentMessage("Added {amount} to {player}'s balance.")
                            .replace("{player}", data.getNickname())
                            .replace("{amount}", EconomyConfig.getInstance().format(amount))
                            .send(ctx.getSource().getSender());
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> take() {
        return Commands.literal("take")
            .then(
                Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                    .executes(ctx -> {
                        OfflinePlayer player = ctx.getArgument("player", OfflinePlayer.class);
                        double amount = ctx.getArgument("amount", double.class);

                        PlayerData data = PlayerData.playerData(player.getUniqueId());
                        data.decrementBalance(amount);

                        ComponentMessage.componentMessage("Taken {amount} from {player}'s balance.")
                            .replace("{player}", data.getNickname())
                            .replace("{amount}", EconomyConfig.getInstance().format(amount))
                            .send(ctx.getSource().getSender());
                        return 1;
                    })
            );
    }

    private ArgumentBuilder<CommandSourceStack, ?> transfer() {
        return Commands.literal("transfer")
            .then(
                Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                    .then(
                        Commands.argument("target", OfflinePlayerArgument.create())
                            .executes(ctx -> {
                                OfflinePlayer player = ctx.getArgument("player", OfflinePlayer.class);
                                OfflinePlayer target = ctx.getArgument("target", OfflinePlayer.class);
                                double amount = ctx.getArgument("amount", double.class);

                                PlayerData playerData = PlayerData.playerData(player.getUniqueId());
                                PlayerData targetData = PlayerData.playerData(target.getUniqueId());

                                if (playerData.getBalance() < amount) {
                                    ComponentMessage.componentMessage("{player} does not have {amount} to transfer.")
                                        .replace("{player}", playerData.getNickname())
                                        .replace("{amount}", EconomyConfig.getInstance().format(amount))
                                        .send(ctx.getSource().getSender());
                                    return 1;
                                }
                                playerData.decrementBalance(amount);
                                targetData.incrementBalance(amount);
                                ComponentMessage.componentMessage("Transferred {amount} from {player} to {target}.")
                                    .replace("{player}", playerData.getNickname())
                                    .replace("{target}", targetData.getNickname())
                                    .replace("{amount}", EconomyConfig.getInstance().format(amount))
                                    .send(ctx.getSource().getSender());
                                return 1;
                            })
                    )
            );
    }

}
