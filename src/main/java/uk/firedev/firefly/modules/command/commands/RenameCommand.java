package uk.firedev.firefly.modules.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.utils.StringUtils;

public class RenameCommand implements Command {

    @NotNull
    @Override
    public String getConfigName() {
        return "rename";
    }

    @NotNull
    @Override
    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal(getCommandName())
            .requires(stack -> isConfigEnabled() && stack.getSender().hasPermission(permission()))
            .then(greedyArg())
            .then(
                Commands.argument("target", PlayerArgument.create())
                    .requires(stack -> stack.getSender().hasPermission(targetPermission()))
                    .then(greedyArg())
            )
            .build();
    }

    // Convenience

    private ArgumentBuilder<CommandSourceStack, ?> greedyArg() {
        return Commands.argument("name", StringArgumentType.greedyString())
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                Player target;
                try {
                    target = context.getArgument("target", Player.class);
                } catch (Exception exception) {
                    target = player;
                }
                String name = context.getArgument("name", String.class);
                setItemName(name, target, player);
                return 1;
            });
    }

    private void setItemName(@NotNull String itemName, @NotNull Player target, @NotNull CommandSender sender) {
        ItemStack handItem = target.getInventory().getItemInMainHand();
        if (handItem.isEmpty()) {
            getHoldItemMessage().send(sender);
            return;
        }
        Component newName;
        if (itemName.equalsIgnoreCase("remove")) {
            handItem.editMeta(meta -> meta.customName(null));
            newName = handItem.displayName();
        } else {
            newName = StringUtils.getColorOnlyComponent(itemName);
            handItem.editMeta(meta -> meta.customName(newName));
        }
        target.getInventory().setItemInMainHand(handItem);

        sendRenamedMessage(newName, target, sender);
    }

    private void sendRenamedMessage(@NotNull Component newName, @NotNull Player target, @NotNull CommandSender sender) {
        getRenamedMessage()
            .replace("{newName}", newName)
            .send(target);
        if (!target.equals(sender)) {
            getRenamedSenderMessage()
                .replace("{newName}", newName)
                .replace("{target}", target.name())
                .send(sender);
        }
    }

    // Messages

    public ComponentMessage getHoldItemMessage() {
        return getMessage("hold-an-item", "{prefix}<red>Please hold an item.");
    }

    public ComponentMessage getRenamedMessage() {
        return getMessage("renamed", "{prefix}<#F0E68C>Renamed your item to {newName}");
    }

    public ComponentMessage getRenamedSenderMessage() {
        return getMessage("renamed-sender", "{prefix}<#F0E68C>Renamed {target}'s item to {newName}");
    }

}
