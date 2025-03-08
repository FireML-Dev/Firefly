package uk.firedev.firefly.modules.teleportation.commands.tpa;

import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;

import java.util.Objects;

public class TPDenyCommand extends CommandAPICommand {

    private static TPDenyCommand instance;

    private TPDenyCommand() {
        super("tpdeny");
        withShortDescription("Deny tpa requests.");
        withFullDescription("Deny tpa requests.");
        executesPlayer((player, arguments) -> {
            TPAHandler.getInstance().denyRequest(player);
        });
    }

    public static TPDenyCommand getInstance() {
        if (instance == null) {
            instance = new TPDenyCommand();
        }
        return instance;
    }

}
