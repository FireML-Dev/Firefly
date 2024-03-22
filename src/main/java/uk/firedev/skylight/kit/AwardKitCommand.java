package uk.firedev.skylight.kit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.command.ICommand;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.skylight.config.MessageConfig;

import java.util.List;

public class AwardKitCommand implements ICommand {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        switch (args.length) {
            case 0,1 -> MessageConfig.getInstance().sendMessageFromConfig(sender, "messages.kits.usage");
            default -> {
                String playerName = args[0];
                Player player = Bukkit.getPlayer(playerName);
                if (player == null) {
                    MessageConfig.getInstance().sendMessageFromConfig(sender, "messages.player-not-found");
                    return true;
                }
                String kitName = args[1];
                Kit kit;
                try {
                    kit = new Kit(kitName);
                } catch (InvalidConfigurationException ex) {
                    MessageConfig.getInstance().sendMessageFromConfig(sender, "messages.kits.kit-not-found");
                    return true;
                }
                ItemUtils.giveItem(kit.buildItem(), player);
                MessageConfig.getInstance().sendMessageFromConfig(sender, "messages.kits.awarded-command",
                        "kit", kit.getName(),
                        "player", player.getName()
                );
                MessageConfig.getInstance().sendMessageFromConfig(player, "messages.kits.awarded-receive",
                        "kit", kit.getName()
                );
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return List.of();
        }
        return switch (args.length) {
            case 1 -> processTabCompletions(args[0], Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            case 2 -> processTabCompletions(args[1], KitManager.getInstance().getKits().stream().map(Kit::getName).toList());
            default -> List.of();
        };
    }
}
