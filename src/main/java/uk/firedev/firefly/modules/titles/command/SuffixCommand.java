package uk.firedev.firefly.modules.titles.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.firefly.modules.titles.TitleConfig;
import uk.firedev.firefly.modules.titles.TitleModule;
import uk.firedev.firefly.modules.titles.gui.PrefixGui;
import uk.firedev.firefly.modules.titles.gui.SuffixGui;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

public class SuffixCommand {

    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("suffix")
            .requires(stack -> TitleModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission("firefly.command.suffix"))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                new SuffixGui(player).open();
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
                Component suffix = TitleModule.getInstance().getPlayerPrefix(player);
                if (suffix == null || ComponentMessage.componentMessage(suffix).isEmpty()) {
                    suffix = Component.text("None");
                }
                TitleConfig.getInstance().getSuffixDisplayMessage()
                    .replace("{player-suffix}", suffix)
                    .send(player);
                return 1;
            });
    }

}
