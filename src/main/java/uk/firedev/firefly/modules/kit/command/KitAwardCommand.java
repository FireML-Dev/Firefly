package uk.firedev.firefly.modules.kit.command;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandPermission;
import uk.firedev.daisylib.libs.commandapi.arguments.*;
import uk.firedev.firefly.modules.kit.Kit;
import uk.firedev.firefly.modules.kit.KitConfig;
import uk.firedev.firefly.modules.kit.KitModule;

import java.util.Objects;

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
                        Player player = (Player) Objects.requireNonNull(arguments.get("player"));
                        Kit kit = (Kit) Objects.requireNonNull(arguments.get("kit"));
                        kit.giveToPlayer(player, sender);
                    });
        }
        return command;
    }

    private static Argument<Kit> getKitArgument() {
        return new CustomArgument<>(new StringArgument("kit"), info -> {
            Kit kit = KitModule.getInstance().getKit(info.input());
            if (kit == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(
                        new CustomArgument.MessageBuilder("Unknown kit: ").appendArgInput()
                );
            }
            return kit;
        });
    }

}
