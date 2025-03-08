package uk.firedev.firefly.modules.teleportation.commands.tpa;

import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;
import uk.firedev.firefly.modules.teleportation.tpa.TPARequest;

import java.util.Objects;

public class TPAHereCommand {

    public static CommandTree getCommand() {
        return new CommandTree("tpahere")
            .withPermission("firefly.command.tpa")
            .withHelp("Request teleports to you.", "Request teleports to you.")
            .then(
                PlayerArgument.create("target")
                    .executesPlayer(info -> {
                        Player target = Objects.requireNonNull(info.args().getUnchecked("target"));
                        TPAHandler.getInstance().sendRequest(target, info.sender(), TPARequest.TPADirection.TARGET_TO_SENDER);
                    })
            );
    }
    
}
