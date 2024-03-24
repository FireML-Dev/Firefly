package uk.firedev.skylight.kit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.ArgumentSuggestions;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.skylight.config.MessageConfig;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class AwardKitCommand extends CommandAPICommand {

    private static AwardKitCommand instance;

    private AwardKitCommand() {
        super("awardkit");
        setPermission(CommandPermission.fromString("skylight.command.awardkit"));
        withShortDescription("Give kits to people");
        withFullDescription("Give kits to people");
        withArguments(getPlayerArgument());
        withArguments(getKitArgument());
        executes((sender, arguments) -> {
            String[] args = arguments.rawArgs();
            if (args.length < 2) {
                MessageConfig.getInstance().sendMessageFromConfig(sender, "messages.kits.usage");
            } else {
                String playerName = args[0];
                Player player = Bukkit.getPlayer(playerName);
                if (player == null) {
                    MessageConfig.getInstance().sendMessageFromConfig(sender, "messages.player-not-found");
                    return;
                }
                String kitName = args[1];
                Kit kit;
                try {
                    kit = new Kit(kitName);
                } catch (InvalidConfigurationException ex) {
                    MessageConfig.getInstance().sendMessageFromConfig(sender, "messages.kits.kit-not-found");
                    return;
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
        });
    }

    public static AwardKitCommand getInstance() {
        if (instance == null) {
            instance = new AwardKitCommand();
        }
        return instance;
    }

    private Argument<?> getPlayerArgument() {
        System.out.println(Arrays.toString(Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)));
        return new StringArgument("player").includeSuggestions(ArgumentSuggestions.stringsAsync(info ->
                CompletableFuture.supplyAsync(() ->
                        Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new))));
    }

    private Argument<?> getKitArgument() {
        return new StringArgument("kit").includeSuggestions(ArgumentSuggestions.strings(
                KitManager.getInstance().getKits().stream().map(Kit::getName).toArray(String[]::new)
        ));
    }

}
