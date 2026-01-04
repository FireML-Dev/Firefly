package uk.firedev.firefly.modules.command.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.util.Utils;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.command.Command;

import java.util.List;
import java.util.Objects;

public class RideCommand implements Command {

    @NotNull
    @Override
    public String getConfigName() {
        return "ride";
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
                RayTraceResult result = player.rayTraceEntities(5);
                Entity entity;
                if (result == null || (entity = result.getHitEntity()) == null) {
                    getTargetNotFoundMessage().send(player);
                    return 1;
                }
                if (getBlacklistedEntities().contains(entity.getType())) {
                    getNotPermittedMessage().send(player);
                    return 1;
                }
                mount(player, player, entity);
                return 1;
            })
            .then(
                Commands.literal("shake")
                    .executes(context -> {
                        Player player = CommandUtils.requirePlayer(context.getSource());
                        if (player == null) {
                            return 1;
                        }
                        player.getPassengers().forEach(passenger -> {
                            if (passenger instanceof Player) {
                                player.removePassenger(passenger);
                            }
                        });
                        getShookMessage().send(player);
                        return 1;
                    })
            )
            .build();
    }

    // Convenience

    private void mount(@NotNull CommandSender sender, @NotNull Player player, @NotNull Entity targetEntity) {
        if (targetEntity.equals(player)) {
            getNotPermittedMessage().send(player);
            return;
        }
        if (!targetEntity.addPassenger(player)) {
            MessageConfig.getInstance().getErrorOccurredMessage().send(player);
            return;
        }
        getRidingMessage().send(player);
        if (!player.equals(sender)) {
            getRidingSenderMessage()
                .send(sender);
        }
        if (targetEntity instanceof Player) {
            getShakeMessage()
                .replace("{player}", player.name())
                .send(targetEntity);
        }
    }

    // Config

    private List<EntityType> getBlacklistedEntities() {
        return getConfig().getStringList("entity-blacklist")
            .stream()
            .map(typeName -> Utils.getEnumValue(EntityType.class, typeName))
            .filter(Objects::nonNull)
            .toList();
    }
    
    // Messages

    public ComponentMessage getTargetNotFoundMessage() {
        return getMessage("target-not-found", "{prefix}<color:#F0E68C>{target}'s fly speed has been set to {speed}.");
    }

    public ComponentMessage getNotPermittedMessage() {
        return getMessage("not-permitted", "{prefix}<red>You are not allowed to ride this entity!");
    }

    public ComponentMessage getRidingMessage() {
        return getMessage("riding", "{prefix}<#F0E68C>You are now riding an entity. Sneak to dismount.");
    }

    public ComponentMessage getRidingSenderMessage() {
        return getMessage("riding-sender", "{prefix}<#F0E68C>{target} is now riding an entity.");
    }

    public ComponentMessage getShakeMessage() {
        return getMessage("shake", "{prefix}<#F0E68C>{player} is now riding you. Type <gold><click:run_command:'/ride shake'>/ride shake</click> <#F0E68C>to get them off!");
    }

    public ComponentMessage getShookMessage() {
        return getMessage("shook", "{prefix}<#F0E68C>Successfully shook off all players!");
    }

}
