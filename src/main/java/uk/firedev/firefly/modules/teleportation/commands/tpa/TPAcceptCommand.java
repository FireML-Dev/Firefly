package uk.firedev.firefly.modules.teleportation.commands.tpa;

import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.firefly.modules.teleportation.tpa.TPAHandler;

public class TPAcceptCommand {

    public static CommandTree getCommand() {
        return new CommandTree("tpaccept")
            .withPermission("firefly.command.tpa")
            .withHelp("Accept tpa requests.", "Accept tpa requests.")
            .executesPlayer(info -> {
                TPAHandler.getInstance().acceptRequest(info.sender());
            });
    }

}
