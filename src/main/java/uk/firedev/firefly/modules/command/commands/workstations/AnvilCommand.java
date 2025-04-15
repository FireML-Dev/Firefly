package uk.firedev.firefly.modules.command.commands.workstations;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.modules.command.Command;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class AnvilCommand extends Command {

    private void open(@NotNull Player player) {
        player.openInventory(
            MenuType.ANVIL.create(player, (Component) null)
        );
    }

    @NotNull
    @Override
    public String getConfigName() {
        return "anvil";
    }

    @NotNull
    @Override
    public CommandTree loadCommand() {
        return new CommandTree(getName())
            .withPermission(getPermission())
            .withAliases(getAliases())
            .executesPlayer(info -> {
                if (disabledCheck(info.sender())) {
                    return;
                }
                open(info.sender());
            })
            .then(new EntitySelectorArgument.OnePlayer("target")
                .withPermission(getTargetPermission())
                .executes(info -> {
                    if (disabledCheck(info.sender())) {
                        return;
                    }
                    Player player = (Player) Objects.requireNonNull(info.args().get("target"));
                    open(player);
                })
            );
    }

}
