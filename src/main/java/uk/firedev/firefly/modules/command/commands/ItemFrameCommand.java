package uk.firedev.firefly.modules.command.commands;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.modules.command.CommandConfig;

public class ItemFrameCommand extends Command {

    private ItemFrame getFrame(@NotNull Player player) {
        RayTraceResult result = player.rayTraceEntities(5);
        Entity entity;
        if (result == null || (entity = result.getHitEntity()) == null) {
            CommandConfig.getInstance().getItemFrameLookAtFrameMessage().sendMessage(player);
            return null;
        }
        if (!(entity instanceof ItemFrame itemFrame)) {
            CommandConfig.getInstance().getItemFrameLookAtFrameMessage().sendMessage(player);
            return null;
        }
        return itemFrame;
    }

    private void invisible(@NotNull Player player, @NotNull ItemFrame frame) {
        if (frame.isInvisible()) {
            frame.setInvisible(false);
            CommandConfig.getInstance().getItemFrameInvisibleOffMessage().sendMessage(player);
        } else {
            frame.setInvisible(true);
            CommandConfig.getInstance().getItemFrameInvisibleOnMessage().sendMessage(player);
        }
    }

    private void fixed(@NotNull Player player, @NotNull ItemFrame frame) {
        if (frame.isFixed()) {
            frame.setFixed(false);
            CommandConfig.getInstance().getItemFrameFixedOffMessage().sendMessage(player);
        } else {
            frame.setFixed(true);
            CommandConfig.getInstance().getItemFrameFixedOnMessage().sendMessage(player);
        }
    }

    private void invulnerable(@NotNull Player player, @NotNull ItemFrame frame) {
        if (frame.isInvulnerable()) {
            frame.setInvulnerable(false);
            CommandConfig.getInstance().getItemFrameInvulnerableOffMessage().sendMessage(player);
        } else {
            frame.setInvulnerable(true);
            CommandConfig.getInstance().getItemFrameInvulnerableOnMessage().sendMessage(player);
        }
    }

    @NotNull
    @Override
    public String getConfigName() {
        return "itemframe";
    }

    @NotNull
    @Override
    public CommandTree refreshCommand() {
        return new CommandTree(getName())
                .withAliases(getAliases())
                .withPermission(getPermission())
                .then(
                        new LiteralArgument("invisible")
                                .executesPlayer(info -> {
                                    ItemFrame frame = getFrame(info.sender());
                                    if (frame == null) {
                                        return;
                                    }
                                    invisible(info.sender(), frame);
                                })
                )
                .then(
                        new LiteralArgument("fixed")
                                .executesPlayer(info -> {
                                    ItemFrame frame = getFrame(info.sender());
                                    if (frame == null) {
                                        return;
                                    }
                                    fixed(info.sender(), frame);
                                })
                )
                .then(
                        new LiteralArgument("invulnerable")
                                .executesPlayer(info -> {
                                    ItemFrame frame = getFrame(info.sender());
                                    if (frame == null) {
                                        return;
                                    }
                                    invulnerable(info.sender(), frame);
                                })
                );
    }

}
