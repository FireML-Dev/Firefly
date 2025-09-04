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

    private List<EntityType> getBlacklistedEntities() {
        return CommandConfig.getInstance().getConfig().getStringList(getConfigName() + ".entity-blacklist")
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
            CommandConfig.getInstance().getRideNotPermittedMessage().send(player);
            return;
        }
        if (!targetEntity.addPassenger(player)) {
            MessageConfig.getInstance().getErrorOccurredMessage().send(player);
            return;
        }
        CommandConfig.getInstance().getRideRidingMessage().send(player);
        if (!player.equals(sender)) {
            CommandConfig.getInstance().getRideRidingSenderMessage()
                .send(sender);
        }
        if (targetEntity instanceof Player) {
            CommandConfig.getInstance().getRideShakeMessage()
                .replace("{player}", player.name())
                .send(targetEntity);
        }
    }

    @NotNull
    @Override
    public String getConfigName() {
        return "ride";
    }

    @NotNull
    @Override
    public CommandTree loadCommand() {
        return new CommandTree(getName())
            .withAliases(getAliases())
            .withPermission(getPermission())
            .executesPlayer(info -> {
                if (disabledCheck(info.sender())) {
                    return;
                }
                RayTraceResult result = info.sender().rayTraceEntities(5);
                Entity entity;
                if (result == null || (entity = result.getHitEntity()) == null) {
                    CommandConfig.getInstance().getRideTargetNotFoundMessage().send(info.sender());
                    return;
                }
                if (getBlacklistedEntities().contains(entity.getType())) {
                    CommandConfig.getInstance().getRideNotPermittedMessage().send(info.sender());
                    return;
                }
                mount(info.sender(), info.sender(), entity);
            })
            .then(
                new LiteralArgument("shake")
                    .executesPlayer(info -> {
                        if (disabledCheck(info.sender())) {
                            return;
                        }
                        info.sender().getPassengers().forEach(passenger -> {
                            if (passenger instanceof Player) {
                                info.sender().removePassenger(passenger);
                            }
                        });
                        CommandConfig.getInstance().getRideShookMessage().send(info.sender());
                    })
            );
    }

}
