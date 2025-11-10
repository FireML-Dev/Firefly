package uk.firedev.firefly.modules.titles.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.modules.titles.TitleConfig;
import uk.firedev.firefly.modules.titles.TitleModule;
import uk.firedev.firefly.modules.titles.gui.PrefixGui;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;

import java.awt.*;
import java.util.List;

public class PrefixCommand implements CommandHolder {

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("prefix")
            .requires(stack -> TitleModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission(permission()))
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

    /**
     * @return The list of aliases this command should have.
     */
    @NotNull
    @Override
    public List<String> aliases() {
        return List.of();
    }

    /**
     * @return The permission for executing this command on yourself.
     */
    @NotNull
    @Override
    public String permission() {
        return "firefly.command.prefix";
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @NotNull
    @Override
    public String targetPermission() {
        return "firefly.command.prefix";
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    public String description() {
        return null;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> display() {
        return Commands.literal("display")
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                Component prefix = TitleModule.getInstance().getPlayerPrefix(player);
                if (prefix == null || ComponentMessage.componentMessage(prefix).isEmpty()) {
                    prefix = Component.text("None");
                }
                TitleConfig.getInstance().getPrefixDisplayMessage()
                    .replace("{player-prefix}", prefix)
                    .send(player);
                return 1;
            });
    }

}
