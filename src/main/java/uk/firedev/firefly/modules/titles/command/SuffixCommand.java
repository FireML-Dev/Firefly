package uk.firedev.firefly.modules.titles.command;

import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.firefly.modules.titles.TitleConfig;
import uk.firedev.firefly.modules.titles.TitleModule;
import uk.firedev.firefly.modules.titles.gui.SuffixGui;

public class SuffixCommand {

    public static CommandTree getCommand() {
        return new CommandTree("suffix")
            .withPermission("firefly.command.suffix")
            .withHelp("Manage Suffix", "Manage Suffix")
            .executesPlayer(info -> {
                new SuffixGui(info.sender()).open();
            })
            .then(getDisplayBranch());
    }


    private static Argument<String> getDisplayBranch() {
        return new StringArgument("display")
            .executesPlayer((player, arguments) -> {
                ComponentMessage suffix = ComponentMessage.of(TitleModule.getInstance().getPlayerSuffix(player));
                if (suffix.isEmpty()) {
                    suffix = ComponentMessage.fromString("None");
                }
                TitleConfig.getInstance().getSuffixDisplayMessage()
                    .replace("player-suffix", suffix.getMessage())
                    .sendMessage(player);
            });
    }

}
