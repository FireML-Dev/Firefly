package uk.firedev.firefly.modules.teleportation.commands.tpa;

import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;
import uk.firedev.firefly.modules.teleportation.tpa.TPARequest;

import java.util.Objects;

public class TPACommand extends CommandAPICommand {

    private static TPACommand instance;

    private TPACommand() {
        super("tpa");
        withArguments(new EntitySelectorArgument.OnePlayer("target"));
        setPermission(CommandPermission.fromString("firefly.command.tpa"));
        withShortDescription("Request teleports.");
        withFullDescription("Request teleports.");
        executesPlayer((player, arguments) -> {
            Player target = (Player) Objects.requireNonNull(arguments.get("target"));
            TPAHandler.getInstance().sendRequest(target, player, TPARequest.TPADirection.SENDER_TO_TARGET);
        });
    }

    public static TPACommand getInstance() {
        if (instance == null) {
            instance = new TPACommand();
        }
        return instance;
    }

}
