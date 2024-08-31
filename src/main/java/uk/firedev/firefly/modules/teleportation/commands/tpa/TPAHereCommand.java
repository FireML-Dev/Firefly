package uk.firedev.firefly.modules.teleportation.commands.tpa;

import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;
import uk.firedev.firefly.modules.teleportation.tpa.TPARequest;

import java.util.Objects;

public class TPAHereCommand extends CommandAPICommand {

    private static TPAHereCommand instance;

    private TPAHereCommand() {
        super("tpahere");
        withArguments(new EntitySelectorArgument.OnePlayer("target"));
        setPermission(CommandPermission.fromString("firefly.command.tpahere"));
        withShortDescription("Request teleports to you.");
        withFullDescription("Request teleports to you.");
        executesPlayer((player, arguments) -> {
            Player target = (Player) Objects.requireNonNull(arguments.get("target"));
            TPAHandler.getInstance().sendRequest(target, player, TPARequest.TPADirection.TARGET_TO_SENDER);
        });
    }

    public static TPAHereCommand getInstance() {
        if (instance == null) {
            instance = new TPAHereCommand();
        }
        return instance;
    }
    
}
