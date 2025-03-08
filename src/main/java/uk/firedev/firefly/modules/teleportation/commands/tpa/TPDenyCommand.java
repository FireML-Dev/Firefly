package uk.firedev.firefly.modules.teleportation.commands.tpa;

import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;

public class TPDenyCommand {

    public static CommandTree getCommand() {
        return new CommandTree("tpdeny")
            .withPermission("firefly.command.tpa")
            .withHelp("Deny tpa requests.", "Deny tpa requests.")
            .executesPlayer(info -> {
                TPAHandler.getInstance().denyRequest(info.sender());
            });
    }

}
