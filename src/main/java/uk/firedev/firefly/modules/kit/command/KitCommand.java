package uk.firedev.firefly.modules.kit.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.firefly.modules.kit.Kit;
import uk.firedev.firefly.modules.kit.KitGui;
import uk.firedev.firefly.modules.kit.KitModule;

public class KitCommand {

    // TODO /kits alias
    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("kit")
            .requires(stack -> KitModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission("firefly.command.kit"))
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
