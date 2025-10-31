package uk.firedev.firefly.modules.titles.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.modules.titles.TitleConfig;
import uk.firedev.firefly.modules.titles.TitleModule;
import uk.firedev.firefly.modules.titles.gui.PrefixGui;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

import java.awt.*;

public class PrefixCommand {

    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("prefix")
            .requires(stack -> stack.getSender().hasPermission("firefly.command.prefix"))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                new PrefixGui(player).open();
                return 1;
            })
            .then(display())
            .build();
    }


    private static ArgumentBuilder<CommandSourceStack, ?> display() {
        return Commands.literal("display")
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                ComponentSingleMessage prefix = ComponentMessage.componentMessage(TitleModule.getInstance().getPlayerPrefix(player));
                if (prefix.isEmpty()) {
                    prefix = ComponentMessage.componentMessage(Component.text("None"));
                }
                TitleConfig.getInstance().getPrefixDisplayMessage()
                    .replace("{player-prefix}", prefix)
                    .send(player);
                return 1;
            });
    }

}
