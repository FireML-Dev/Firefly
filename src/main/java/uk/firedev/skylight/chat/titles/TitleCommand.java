package uk.firedev.skylight.chat.titles;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.command.ICommand;

import java.util.List;

public class TitleCommand implements ICommand {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        switch (args.length) {
            case 1 -> {
                switch (args[0]) {
                    case "apply" -> {
                        TitleManager.getInstance().setPlayerPrefix(player, "<rainbow>Your Fancy Prefix</rainbow>");
                        player.sendMessage("Title Updated");
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

}
