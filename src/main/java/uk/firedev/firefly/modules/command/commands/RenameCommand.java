package uk.firedev.firefly.modules.command.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.ArgumentSuggestions;
import uk.firedev.daisylib.libs.commandapi.arguments.GreedyStringArgument;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.modules.command.CommandConfig;
import uk.firedev.firefly.utils.StringUtils;

import java.util.Objects;

public class RenameCommand extends Command {

    public RenameCommand() {
        super();
    }

    @NotNull
    @Override
    public String getConfigName() {
        return "rename";
    }

    @NotNull
    @Override
    public CommandTree loadCommand() {
        Argument<String> greedy = new GreedyStringArgument("itemName")
            .includeSuggestions(ArgumentSuggestions.strings("remove"))
            .executesPlayer(info -> {
                if (disabledCheck(info.sender())) {
                    return;
                }
                Player player = info.sender();
                Player target = Objects.requireNonNullElse(info.args().getUnchecked("target"), player);
                String itemName = Objects.requireNonNull(info.args().getUnchecked("itemName"));
                setItemName(itemName, target, player);
            });

        return new CommandTree(getName())
            .withAliases(getAliases())
            .withPermission(getPermission())
            .then(greedy)
            .then(
                PlayerArgument.create("target")
                    .withPermission(getTargetPermission()))
                    .then(greedy);
    }

    private void setItemName(@NotNull String itemName, @NotNull Player target, @NotNull CommandSender sender) {
        ItemStack handItem = target.getInventory().getItemInMainHand();
        if (handItem.isEmpty()) {
            CommandConfig.getInstance().getRenameHoldItemMessage().sendMessage(sender);
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
        CommandConfig.getInstance().getRenamedMessage()
            .replace("newName", newName)
            .sendMessage(target);
        if (!target.equals(sender)) {
            CommandConfig.getInstance().getRenamedSenderMessage()
                .replace("newName", newName)
                .replace("target", target.name())
                .sendMessage(sender);
        }
    }

}
