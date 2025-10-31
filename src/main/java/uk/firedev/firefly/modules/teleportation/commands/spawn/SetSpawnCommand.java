package uk.firedev.firefly.modules.teleportation.commands.spawn;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.math.FinePosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;

@SuppressWarnings("UnstableApiUsage")
public class SetSpawnCommand {

    public LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("setspawn")
            .requires(stack -> stack.getSender().hasPermission("firefly.command.setspawn"))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                setLocation(player, player.getLocation());
                return 1;
            })
            .then(
                Commands.argument("world", ArgumentTypes.world())
                        .then(
                            Commands.argument("location", ArgumentTypes.finePosition())
                                .executes(context -> {
                                    World world = context.getArgument("world", World.class);
                                    // TODO ensure this works how i want it to
                                    Location location = context.getArgument("location", FinePosition.class).toLocation(world);
                                    setLocation(context.getSource().getSender(), location);
                                    return 1;
                                })
                        )
            )
            .build();
    }

    private static void setLocation(@NotNull CommandSender sender, @NotNull Location location) {
        TeleportConfig.getInstance().setSpawnLocation(false, location);
        TeleportConfig.getInstance().getSpawnSetSpawnMessage().send(sender);
    }
    
}
