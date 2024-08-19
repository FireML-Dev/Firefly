package uk.firedev.firefly.modules.kit.command;

import org.bukkit.configuration.InvalidConfigurationException;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.ArgumentSuggestions;
import uk.firedev.daisylib.libs.commandapi.arguments.StringArgument;
import uk.firedev.firefly.modules.kit.Kit;
import uk.firedev.firefly.modules.kit.KitConfig;
import uk.firedev.firefly.modules.kit.KitGUI;
import uk.firedev.firefly.modules.kit.KitManager;

import java.util.Objects;

public class KitCommand extends CommandAPICommand {

    private static KitCommand instance;

    private KitCommand() {
        super("kit");
        withAliases("kits");
        setPermission(CommandPermission.fromString("firefly.command.kit"));
        withShortDescription("Get kits");
        withFullDescription("Get kits");
        withSubcommand(KitAwardCommand.getInstance());
        withArguments(getKitArgument());
        executesPlayer((player, arguments) -> {
            String[] args = arguments.rawArgs();
            if (args.length == 0) {
                new KitGUI(player).open();
                return;
            }
            Kit kit;
            try {
                kit = new Kit(args[0]);
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

    public static KitCommand getInstance() {
        if (instance == null) {
            instance = new KitCommand();
        }
        return instance;
    }

    private Argument<?> getKitArgument() {
        return new StringArgument("kit").setOptional(true).includeSuggestions(ArgumentSuggestions.strings(
                KitManager.getInstance().getKits().stream()
                        .map(kit -> kit.isPlayerVisible() ? kit : null)
                        .filter(Objects::nonNull)
                        .map(Kit::getName)
                        .toArray(String[]::new)
        ));
    }

}
