package uk.firedev.firefly.modules.teleportation.commands.spawn;

import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.firefly.modules.teleportation.TeleportModule;

import java.util.Objects;

public class SpawnCommand {

    public static CommandTree getCommand() {
        return new CommandTree("spawn")
            .withPermission("firefly.command.spawn")
            .withHelp("Teleport to spawn", "Teleport to spawn")
            .executesPlayer(info -> {
                TeleportModule.getInstance().sendToSpawn(false, info.sender(), true);
            })
            .then(
                uk.firedev.daisylib.command.arguments.PlayerArgument.create("target")
                    .withPermission("firefly.command.spawn.other")
                    .executes(info -> {
                        Player target = Objects.requireNonNull(info.args().getUnchecked("target"));
                        TeleportModule.getInstance().sendToSpawn(false, target, info.sender(), true);
                    })
            );
    }

}
