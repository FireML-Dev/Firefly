package uk.firedev.skylight.chat.titles;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.command.ICommand;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.skylight.chat.titles.gui.PrefixGUI;

import java.util.List;

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
