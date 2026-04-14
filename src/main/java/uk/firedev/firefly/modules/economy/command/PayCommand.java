package uk.firedev.firefly.modules.economy.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
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

public class PayCommand implements CommandHolder {

    /**
     * @return The command.
     */
    @Override
    public @NonNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("pay")
            .then(
                Commands.argument("target", OfflinePlayerArgument.create())
                    .then(
                        Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                            .executes(ctx -> {
                                Player player = CommandUtils.requirePlayer(ctx);
                                OfflinePlayer target = ctx.getArgument("target", OfflinePlayer.class);
                                if (player.equals(target)) {
                                    ComponentMessage.componentMessage("You cannot send money to yourself!").send(player);
                                    return 1;
                                }

                                double amount = ctx.getArgument("amount", double.class);
                                String amountFormatted = EconomyConfig.getInstance().format(amount);

                                PlayerData playerData = PlayerData.playerData(player.getUniqueId());
                                if (playerData.getBalance() < amount) {
                                    ComponentMessage.componentMessage("You do not have {amount} to send!")
                                        .replace("{amount}", amountFormatted)
                                        .send(player);
                                    return 1;
                                }
                                PlayerData targetData = PlayerData.playerData(target.getUniqueId());
                                playerData.decrementBalance(amount);
                                targetData.incrementBalance(amount);

                                // Notify player
                                ComponentMessage.componentMessage("You have sent {amount} to {target}.")
                                    .replace("{amount}", amountFormatted)
                                    .replace("{target}", targetData.getNickname())
                                    .send(player);

                                // Notify target
                                ComponentMessage.componentMessage("You have been sent {amount} from {player}.")
                                    .replace("{amount}", amountFormatted)
                                    .replace("{player}", playerData.getNickname())
                                    .send(target.getPlayer());
                                return 1;
                            })
                    )
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
        return EconomyModule.PAY_PERMISSION;
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
