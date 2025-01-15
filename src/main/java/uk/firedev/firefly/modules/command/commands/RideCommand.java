package uk.firedev.firefly.modules.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.command.Command;
import uk.firedev.firefly.modules.command.CommandConfig;

import java.util.List;
import java.util.Objects;

public class RideCommand extends Command {

    private final CommandTree command = new CommandTree(getName())
            .executesPlayer((player, arguments) -> {
                RayTraceResult result = player.rayTraceEntities(5);
                Entity entity;
                if (result == null || (entity = result.getHitEntity()) == null) {
                    CommandConfig.getInstance().getRideTargetNotFoundMessage().sendMessage(player);
                    return;
                }
                if (getBlacklistedEntities().contains(entity.getType())) {
                    CommandConfig.getInstance().getRideNotPermittedMessage().sendMessage(player);
                    return;
                }
                mount(player, player, entity);
            })
            .then(
                    new LiteralArgument("shake")
                            .executesPlayer((player, arguments) -> {
                                player.getPassengers().forEach(passenger -> {
                                    if (passenger instanceof Player) {
                                        player.removePassenger(passenger);
                                    }
                                });
                                CommandConfig.getInstance().getRideShookMessage().sendMessage(player);
                            })
            );

    private List<EntityType> getBlacklistedEntities() {
        return CommandConfig.getInstance().getConfig().getStringList(getName() + ".entity-blacklist")
                .stream()
                .map(typeName -> {
                    try {
                        return EntityType.valueOf(typeName.toUpperCase());
                    } catch (IllegalArgumentException exception) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private void mount(@NotNull CommandSender sender, @NotNull Player player, @NotNull Entity targetEntity) {
        if (targetEntity.equals(player)) {
            CommandConfig.getInstance().getRideNotPermittedMessage().sendMessage(player);
            return;
        }
        if (!targetEntity.addPassenger(player)) {
            MessageConfig.getInstance().getErrorOccurredMessage().sendMessage(player);
            return;
        }
        CommandConfig.getInstance().getRideRidingMessage().sendMessage(player);
        if (!player.equals(sender)) {
            CommandConfig.getInstance().getRideRidingSenderMessage()
                    .sendMessage(sender);
        }
        if (targetEntity instanceof Player) {
            CommandConfig.getInstance().getRideShakeMessage()
                    .replace("player", player.name())
                    .sendMessage(targetEntity);
        }
    }

    @Override
    public String getName() {
        return "ride";
    }

    @Override
    public CommandTree getCommand() {
        return command;
    }
}
