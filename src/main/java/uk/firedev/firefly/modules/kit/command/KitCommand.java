package uk.firedev.firefly.modules.kit.command;

import org.bukkit.entity.Player;
import uk.firedev.daisylib.command.arguments.PlayerArgument;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.Argument;
import uk.firedev.daisylib.libs.commandapi.arguments.LiteralArgument;
import uk.firedev.firefly.modules.kit.*;

import java.util.Objects;

public class KitCommand {

    private static CommandAPICommand command;

    private KitCommand() {}

    public static CommandTree getCommand() {
        return new CommandTree("kit")
            .withAliases("kits")
            .withPermission("firefly.command.kit")
            .withShortDescription("Get kits")
            .withFullDescription("Get kits")
            .then(
                KitArgument.createPredicate("kit", (player, kit) -> kit.isPlayerVisible())
                    .executesPlayer(info -> {
                        Kit kit = Objects.requireNonNull(info.args().getUnchecked("kit"));
                        kit.giveToPlayerWithCooldown(info.sender(), null);
                    })
            )
            .then(getAwardBranch());
    }

    private static Argument<String> getAwardBranch() {
        return new LiteralArgument("award")
            .withPermission("firefly.command.kit.award")
            .thenNested(
                KitArgument.create("kit"),
                PlayerArgument.create("player")
                    .executes(info -> {
                        Kit kit = Objects.requireNonNull(info.args().getUnchecked("kit"));
                        Player target = Objects.requireNonNull(info.args().getUnchecked("player"));
                        kit.giveToPlayer(target, info.sender());
                    })
            );
    }



}
