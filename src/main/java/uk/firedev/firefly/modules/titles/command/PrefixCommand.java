package uk.firedev.firefly.modules.titles.command;

import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.firefly.modules.titles.TitleConfig;
import uk.firedev.firefly.modules.titles.TitleModule;
import uk.firedev.firefly.modules.titles.gui.PrefixGUI;

public class PrefixCommand extends CommandAPICommand {

    private static PrefixCommand instance = null;

    private PrefixCommand() {
        super("prefix");
        setPermission(CommandPermission.fromString("firefly.command.prefix"));
        withShortDescription("Manage Prefix");
        withFullDescription("Manage Prefix");
        withSubcommand(getDisplayCommand());
        executesPlayer((player, arguments) -> {
            new PrefixGUI(player).open();
        });
    }

    private CommandAPICommand getDisplayCommand() {
        return new CommandAPICommand("display")
                .executesPlayer((player, arguments) -> {
                    ComponentMessage prefix = ComponentMessage.of(TitleModule.getInstance().getPlayerPrefix(player));
                    if (prefix.isEmpty()) {
                        prefix = ComponentMessage.fromString("None");
                    }
                    TitleConfig.getInstance().getPrefixDisplayMessage()
                            .replace("player-prefix", prefix.getMessage())
                            .sendMessage(player);
                });
    }

    public static PrefixCommand getInstance() {
        if (instance == null) {
            instance = new PrefixCommand();
        }
        return instance;
    }

}
