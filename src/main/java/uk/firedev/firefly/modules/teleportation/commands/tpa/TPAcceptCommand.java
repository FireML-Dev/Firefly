package uk.firedev.firefly.modules.teleportation.commands.tpa;

import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;

public class TPAcceptCommand extends CommandAPICommand {

    private static TPAcceptCommand instance;

    private TPAcceptCommand() {
        super("tpaccept");
        setPermission(CommandPermission.fromString("firefly.command.tpaccept"));
        withShortDescription("Accept tpa requests.");
        withFullDescription("Accept tpa requests.");
        executesPlayer((player, arguments) -> {
            TPAHandler.getInstance().acceptRequest(player);
        });
    }

    public static TPAcceptCommand getInstance() {
        if (instance == null) {
            instance = new TPAcceptCommand();
        }
        return instance;
    }

}
