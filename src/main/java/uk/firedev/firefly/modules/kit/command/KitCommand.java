package uk.firedev.firefly.modules.kit.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.argument.PlayerArgument;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.modules.kit.Kit;
import uk.firedev.firefly.modules.kit.KitGui;
import uk.firedev.firefly.modules.kit.KitModule;

import java.util.List;

public class KitCommand implements CommandHolder {

    @Override
    public @NonNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("kit")
            .requires(stack -> KitModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission(permission()))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                new KitGui(player).open();
                return 1;
            })
            .then(getArg())
            .then(award())
            .build();
    }

    /**
     * @return The list of aliases this command should have.
     */
    @NonNull
    @Override
    public List<String> aliases() {
        return List.of("kits");
    }

    /**
     * @return The permission for executing this command on yourself.
     */
    @NonNull
    @Override
    public String permission() {
        return "firefly.command.kit";
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @NonNull
    @Override
    public String targetPermission() {
        return "firefly.command.kit";
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    public String description() {
        return null;
    }

    private ArgumentBuilder<CommandSourceStack, ?> getArg() {
        return Commands.literal("get")
            .then(
                Commands.argument("kit", KitArgument.create((player, kit) -> kit.isPlayerVisible()))
                    .executes(context -> {
                        Player player = CommandUtils.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        Kit kit = context.getArgument("kit", Kit.class);
                        kit.giveToPlayerWithCooldown(player, null);
                        return 1;
                    })
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> award() {
        return Commands.literal("award")
            .requires(stack -> stack.getSender().hasPermission("firefly.command.kit.award"))
            .then(
                Commands.argument("kit", KitArgument.create())
                    .then(
                        Commands.argument("player", PlayerArgument.create())
                            .executes(context -> {
                                Kit kit = context.getArgument("kit", Kit.class);
                                Player target = context.getArgument("player", Player.class);
                                kit.giveToPlayer(target, context.getSource().getSender());
                                return 1;
                            })
                    )
            );
    }



}
