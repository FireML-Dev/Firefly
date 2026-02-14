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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.modules.teleportation.TeleportConfig;
import uk.firedev.firefly.modules.teleportation.TeleportModule;

import java.util.List;

public class SetSpawnCommand implements CommandHolder {

    @Override
    public @NonNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("setspawn")
            .requires(stack -> TeleportModule.getInstance().isConfigEnabled() && stack.getSender().hasPermission(permission()))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                setLocation(player, player.getLocation());
                return 1;
            })
            .build();
    }

    /**
     * @return The list of aliases this command should have.
     */
    @NonNull
    @Override
    public List<String> aliases() {
        return List.of();
    }

    /**
     * @return The permission for executing this command on yourself.
     */
    @NonNull
    @Override
    public String permission() {
        return "firefly.command.setspawn";
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @NonNull
    @Override
    public String targetPermission() {
        return "firefly.command.setspawn";
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    public String description() {
        return null;
    }

    private static void setLocation(@NonNull CommandSender sender, @NonNull Location location) {
        TeleportConfig.getInstance().setSpawnLocation(false, location);
        TeleportConfig.getInstance().getSpawnSetSpawnMessage().send(sender);
    }
    
}
