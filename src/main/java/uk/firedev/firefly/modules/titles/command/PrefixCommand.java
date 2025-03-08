package uk.firedev.firefly.modules.titles.command;

import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.firefly.modules.titles.TitleConfig;
import uk.firedev.firefly.modules.titles.TitleModule;
import uk.firedev.firefly.modules.titles.gui.PrefixGui;

public class PrefixCommand {

    public static CommandTree getCommand() {
        return new CommandTree("prefix")
            .withPermission("firefly.command.prefix")
            .withHelp("Manage Prefix", "Manage Prefix")
            .executesPlayer(info -> {
                new PrefixGui(info.sender()).open();
            })
            .then(getDisplayBranch());
    }


    private static Argument<String> getDisplayBranch() {
        return new StringArgument("display")
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

}
