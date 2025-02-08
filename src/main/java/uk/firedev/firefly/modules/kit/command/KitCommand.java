package uk.firedev.firefly.modules.kit.command;

import org.bukkit.configuration.InvalidConfigurationException;
import uk.firedev.daisylib.command.ArgumentBuilder;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.firefly.modules.kit.*;

public class KitCommand {

    private static CommandAPICommand command;

    private KitCommand() {}

    public static CommandAPICommand getCommand() {
        if (command == null) {
            command = new CommandAPICommand("kit")
                    .withAliases("kits")
                    .withPermission("firefly.command.kit")
                    .withShortDescription("Get kits")
                    .withFullDescription("Get kits")
                    .withSubcommand(KitAwardCommand.getCommand())
                    .withArguments(getKitArgument())
                    .executesPlayer((player, arguments) -> {
                        Object kitNameObj = arguments.get("kit");
                        if (kitNameObj == null) {
                            new KitGui(player).open();
                            return;
                        }
                        String kitName = (String) kitNameObj;
                        Kit kit;
                        try {
                            kit = new Kit(kitName);
                        } catch (InvalidConfigurationException ex) {
                            KitConfig.getInstance().getNotFoundMessage().sendMessage(player);
                            return;
                        }
                        if (!kit.isPlayerVisible()) {
                            KitConfig.getInstance().getNotFoundMessage().sendMessage(player);
                            return;
                        }
                        kit.awardKit(player, false);
                    });
        }
        return command;
    }

    private static Argument<?> getKitArgument() {
        return new StringArgument("kit").setOptional(true).includeSuggestions(ArgumentBuilder.getAsyncSuggestions(
                info -> KitModule.getInstance().getKits().keySet().toArray(String[]::new)
        ));
    }

}
