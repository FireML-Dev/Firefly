package uk.firedev.firefly.modules.command.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.firefly.modules.command.Command;

public class HealCommand implements Command {

    @NotNull
    @Override
    public String getConfigName() {
        return "heal";
    }

    @NotNull
    @Override
    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal(getCommandName())
            .requires(stack -> isConfigEnabled() && stack.getSender().hasPermission(permission()))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                heal(player, player);
                return 1;
            })
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .requires(stack -> stack.getSender().hasPermission(targetPermission()))
                    .executes(context -> {
                        Player player = context.getArgument("target", Player.class);
                        heal(context.getSource().getSender(), player);
                        return 1;
                    })
            )
            .build();
    }
    
    // Convenience

    private void heal(@NotNull CommandSender sender, @NotNull Player target) {
        double maxHealth = 20;
        AttributeInstance attribute = target.getAttribute(Attribute.MAX_HEALTH);
        if (attribute != null) {
            maxHealth = attribute.getValue();
        }
        target.heal(maxHealth);
        target.setFoodLevel(20);
        target.setSaturation(20);
        sendHealedMessage(sender, target);
    }

    private void sendHealedMessage(@NotNull CommandSender sender, @NotNull Player target) {
        getHealedMessage().send(target);
        if (!target.equals(sender)) {
            getHealedSenderMessage()
                .replace("{target}", target.name())
                .send(sender);
        }
    }
    
    // Messages

    public ComponentMessage getHealedMessage() {
        return getMessage("healed", "{prefix}<color:#F0E68C>You have been healed.");
    }

    public ComponentMessage getHealedSenderMessage() {
        return getMessage("healed-sender", "{prefix}<color:#F0E68C>You have healed {target}.");
    }

}
