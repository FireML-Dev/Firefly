package uk.firedev.firefly.modules.command.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.firefly.modules.command.Command;

public class ItemFrameCommand implements Command {

    @NotNull
    @Override
    public String getConfigName() {
        return "itemframe";
    }

    @NotNull
    @Override
    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal(getCommandName())
            .requires(stack -> isConfigEnabled() && stack.getSender().hasPermission(permission()))
            .then(invisibleArg())
            .then(fixedArg())
            .then(invulnerableArg())
            .build();
    }

    @Override
    public void registerCommands(@NotNull Commands registrar) {
        registrar.register(get());
    }

    private ArgumentBuilder<CommandSourceStack, ?> invisibleArg() {
        return Commands.literal("invisible")
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                ItemFrame frame = getFrame(player);
                if (frame == null) {
                    return 1;
                }
                invisible(player, frame);
                return 1;
            });
    }

    private ArgumentBuilder<CommandSourceStack, ?> fixedArg() {
        return Commands.literal("fixed")
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                ItemFrame frame = getFrame(player);
                if (frame == null) {
                    return 1;
                }
                fixed(player, frame);
                return 1;
            });
    }

    private ArgumentBuilder<CommandSourceStack, ?> invulnerableArg() {
        return Commands.literal("invulnerable")
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                ItemFrame frame = getFrame(player);
                if (frame == null) {
                    return 1;
                }
                invulnerable(player, frame);
                return 1;
            });
    }

    // Convenience

    private ItemFrame getFrame(@NotNull Player player) {
        RayTraceResult result = player.rayTraceEntities(5);
        Entity entity;
        if (result == null || (entity = result.getHitEntity()) == null) {
            getLookAtFrameMessage().send(player);
            return null;
        }
        if (!(entity instanceof ItemFrame itemFrame)) {
            getLookAtFrameMessage().send(player);
            return null;
        }
        return itemFrame;
    }

    private void invisible(@NotNull Player player, @NotNull ItemFrame frame) {
        if (frame.isInvisible()) {
            frame.setInvisible(false);
            getInvisibleOffMessage().send(player);
        } else {
            frame.setInvisible(true);
            getInvisibleOnMessage().send(player);
        }
    }

    private void fixed(@NotNull Player player, @NotNull ItemFrame frame) {
        if (frame.isFixed()) {
            frame.setFixed(false);
            getFixedOffMessage().send(player);
        } else {
            frame.setFixed(true);
            getFixedOnMessage().send(player);
        }
    }

    private void invulnerable(@NotNull Player player, @NotNull ItemFrame frame) {
        if (frame.isInvulnerable()) {
            frame.setInvulnerable(false);
            getInvulnerableOffMessage().send(player);
        } else {
            frame.setInvulnerable(true);
            getInvulnerableOnMessage().send(player);
        }
    }
    
    // Messages

    public ComponentMessage getLookAtFrameMessage() {
        return getMessage("look-at-frame", "{prefix}<red>You must be looking at an item frame!");
    }

    public ComponentMessage getInvisibleOnMessage() {
        return getMessage("invisible-on", "{prefix}<#F0E68C>Item Frame is now invisible!");
    }

    public ComponentMessage getInvisibleOffMessage() {
        return getMessage("invisible-off", "{prefix}<#F0E68C>Item Frame is no longer invisible!");
    }

    public ComponentMessage getFixedOnMessage() {
        return getMessage("fixed-on", "{prefix}<#F0E68C>Item Frame is now fixed!");
    }

    public ComponentMessage getFixedOffMessage() {
        return getMessage("fixed-off", "{prefix}<#F0E68C>Item Frame is no longer fixed!");
    }

    public ComponentMessage getInvulnerableOnMessage() {
        return getMessage("invulnerable-on", "{prefix}<#F0E68C>Item Frame is now invulnerable!");
    }

    public ComponentMessage getInvulnerableOffMessage() {
        return getMessage("invulnerable-off", "{prefix}<#F0E68C>Item Frame is no longer invulnerable!");
    }

}
