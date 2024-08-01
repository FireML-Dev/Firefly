package uk.firedev.firefly.modules.kit.command;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.ArgumentSuggestions;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.kit.Kit;
import uk.firedev.firefly.modules.kit.KitConfig;
import uk.firedev.firefly.modules.kit.KitManager;

import java.util.concurrent.CompletableFuture;

public class AwardKitCommand extends CommandAPICommand {

    private static AwardKitCommand instance;

    private AwardKitCommand() {
        super("awardkit");
        setPermission(CommandPermission.fromString("firefly.command.awardkit"));
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
                ItemUtils.giveItem(kit.buildItem(), player);
                ComponentReplacer replacer = new ComponentReplacer().addReplacements("kit", kit.getName());
                KitConfig.getInstance().getAwardedReceiverMessage().applyReplacer(replacer).sendMessage(player);
                replacer.addReplacements("player", player.getName());
                KitConfig.getInstance().getAwardedCommandMessage().applyReplacer(replacer).sendMessage(sender);
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
