package uk.firedev.skylight.modules.titles.command;

import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.skylight.modules.titles.gui.SuffixGUI;

public class SuffixCommand extends CommandAPICommand {

    private static SuffixCommand instance = null;

    private SuffixCommand() {
        super("suffix");
        setPermission(CommandPermission.fromString("skylight.command.suffix"));
        withShortDescription("Manage Suffix");
        withFullDescription("Manage Suffix");
        executesPlayer((player, arguments) -> {
            new SuffixGUI(player).open();
        });
    }

    public static SuffixCommand getInstance() {
        if (instance == null) {
            instance = new SuffixCommand();
        }
        return instance;
    }

}
