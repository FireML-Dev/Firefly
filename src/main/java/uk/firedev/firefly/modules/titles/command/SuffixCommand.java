package uk.firedev.firefly.modules.titles.command;

import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.firefly.modules.titles.TitleConfig;
import uk.firedev.firefly.modules.titles.TitleModule;
import uk.firedev.firefly.modules.titles.gui.SuffixGUI;

public class SuffixCommand extends CommandAPICommand {

    private static SuffixCommand instance = null;

    private SuffixCommand() {
        super("suffix");
        setPermission(CommandPermission.fromString("firefly.command.suffix"));
        withShortDescription("Manage Suffix");
        withFullDescription("Manage Suffix");
        withSubcommand(getDisplayCommand());
        executesPlayer((player, arguments) -> {
            new SuffixGUI(player).open();
        });
    }

    private CommandAPICommand getDisplayCommand() {
        return new CommandAPICommand("display")
                .executesPlayer((player, arguments) -> {
                    ComponentMessage suffix = ComponentMessage.of(TitleModule.getInstance().getPlayerPrefix(player));
                    if (suffix.isEmpty()) {
                        suffix = ComponentMessage.fromString("None");
                    }
                    TitleConfig.getInstance().getSuffixDisplayMessage()
                            .replace("player-suffix", suffix.getMessage())
                            .sendMessage(player);
                });
    }

    public static SuffixCommand getInstance() {
        if (instance == null) {
            instance = new SuffixCommand();
        }
        return instance;
    }

}
