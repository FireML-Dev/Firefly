package uk.firedev.firefly.modules.titles.command;

import net.kyori.adventure.text.Component;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.firefly.modules.titles.TitleConfig;
import uk.firedev.firefly.modules.titles.TitleModule;
import uk.firedev.firefly.modules.titles.gui.PrefixGui;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

import java.awt.*;

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
                    ComponentSingleMessage prefix = ComponentMessage.componentMessage(TitleModule.getInstance().getPlayerPrefix(player));
                    if (prefix.isEmpty()) {
                        prefix = ComponentMessage.componentMessage(Component.text("None"));
                    }
                    TitleConfig.getInstance().getPrefixDisplayMessage()
                            .replace("{player-prefix}", prefix)
                            .send(player);
                });
    }

}
