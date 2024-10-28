package uk.firedev.firefly.modules.kit.command;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.ArgumentSuggestions;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.kit.Kit;
import uk.firedev.firefly.modules.kit.KitConfig;
import uk.firedev.firefly.modules.kit.KitModule;

import java.util.concurrent.CompletableFuture;


public class KitAwardCommand extends CommandAPICommand {

    private static KitAwardCommand instance;

    private KitAwardCommand() {
        super("award");
        setPermission(CommandPermission.fromString("firefly.command.kit.award"));
        withShortDescription("Give kits to people");
        withFullDescription("Give kits to people");
        withArguments(getPlayerArgument());
        withArguments(getKitArgument());
        executes((sender, arguments) -> {
            String[] args = arguments.rawArgs();
            if (args.length < 2) {
                KitConfig.getInstance().getUsageMessage().sendMessage(sender);
            } else {
                String playerName = args[0];
                Player player = Bukkit.getPlayer(playerName);
                if (player == null) {
                    MessageConfig.getInstance().getPlayerNotFoundMessage().sendMessage(sender);
                    return;
                }
                String kitName = args[1];
                Kit kit;
                try {
                    kit = new Kit(kitName);
                } catch (InvalidConfigurationException ex) {
                    KitConfig.getInstance().getNotFoundMessage().sendMessage(sender);
                    return;
                }
                kit.giveToPlayer(player, sender);
            }
        });
    }

    public static KitAwardCommand getInstance() {
        if (instance == null) {
            instance = new KitAwardCommand();
        }
        return instance;
    }

    private Argument<?> getPlayerArgument() {
        return new StringArgument("player").includeSuggestions(ArgumentSuggestions.stringsAsync(info ->
                CompletableFuture.supplyAsync(() ->
                        Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new))));
    }

    private Argument<?> getKitArgument() {
        return new StringArgument("kit").includeSuggestions(ArgumentSuggestions.strings(
                KitModule.getInstance().getKits().stream().map(Kit::getName).toArray(String[]::new)
        ));
    }

}
