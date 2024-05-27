package uk.firedev.skylight.modules.titles.command;

import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.skylight.modules.titles.gui.PrefixGUI;

public class PrefixCommand extends CommandAPICommand {

    private static PrefixCommand instance = null;

    private PrefixCommand() {
        super("prefix");
        setPermission(CommandPermission.fromString("skylight.command.prefix"));
        withShortDescription("Manage Prefix");
        withFullDescription("Manage Prefix");
        executesPlayer((player, arguments) -> {
            new PrefixGUI(player).open();
        });
    }

    public static PrefixCommand getInstance() {
        if (instance == null) {
            instance = new PrefixCommand();
        }
        return instance;
    }

}
