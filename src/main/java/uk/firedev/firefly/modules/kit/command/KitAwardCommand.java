package uk.firedev.firefly.modules.kit.command;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.*;
import uk.firedev.firefly.modules.kit.Kit;
import uk.firedev.firefly.modules.kit.KitConfig;
import uk.firedev.firefly.modules.kit.KitModule;

public class KitAwardCommand {

    private static CommandAPICommand command;

    private KitAwardCommand() {}

    public static CommandAPICommand getCommand() {
        if (command == null) {
            command = new CommandAPICommand("award")
                    .withPermission(CommandPermission.fromString("firefly.command.kit.award"))
                    .withShortDescription("Give kits to people")
                    .withFullDescription("Give kits to people")
                    .withArguments(new EntitySelectorArgument.OnePlayer("player"), getKitArgument())
                    .executes((sender, arguments) -> {
                        Object playerObj = arguments.get("player");
                        Object kitObj = arguments.get("kit");
                        if (playerObj == null || kitObj == null) {
                            KitConfig.getInstance().getUsageMessage().sendMessage(sender);
                        }
                        Player player = (Player) playerObj;
                        String kitName = (String) kitObj;
                        Kit kit;
                        try {
                            kit = new Kit(kitName);
                        } catch (InvalidConfigurationException ex) {
                            KitConfig.getInstance().getNotFoundMessage().sendMessage(sender);
                            return;
                        }
                        kit.giveToPlayer(player, sender);
                    });
        }
        return command;
    }

    private static Argument<?> getKitArgument() {
        return new StringArgument("kit").includeSuggestions(ArgumentSuggestions.strings(
                KitModule.getInstance().getKits().stream().map(Kit::getName).toArray(String[]::new)
        ));
    }

}
