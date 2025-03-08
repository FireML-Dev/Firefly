package uk.firedev.firefly.modules.teleportation.commands.tpa;

import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;
import uk.firedev.firefly.modules.teleportation.tpa.TPARequest;

import java.util.Objects;

public class TPACommand {

    public static CommandTree getCommand() {
        return new CommandTree("tpa")
            .withPermission("firefly.command.tpa")
            .withHelp("Request teleports.", "Request teleports.")
            .then(
                PlayerArgument.create("target")
                    .executesPlayer(info -> {
                        Player target = Objects.requireNonNull(info.args().getUnchecked("target"));
                        TPAHandler.getInstance().sendRequest(target, info.sender(), TPARequest.TPADirection.SENDER_TO_TARGET);
                    })
            );
    }

}
