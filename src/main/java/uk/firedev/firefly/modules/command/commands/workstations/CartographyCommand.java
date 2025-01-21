package uk.firedev.firefly.modules.command.commands.workstations;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.modules.command.Command;

import java.util.Objects;

public class CartographyCommand extends Command {

    private void open(@NotNull Player player) {
        player.openInventory(
                MenuType.CARTOGRAPHY_TABLE.create(player, Component.empty())
        );
    }

    @NotNull
    @Override
    public String getConfigName() {
        return "cartography";
    }

    @NotNull
    @Override
    public CommandTree refreshCommand() {
        return new CommandTree(getName())
                .withAliases(getAliases())
                .withPermission(getPermission())
                .executesPlayer(info -> {
                    open(info.sender());
                })
                .then(new EntitySelectorArgument.OnePlayer("target")
                        .withPermission(getTargetPermission())
                        .executes((sender, arguments) -> {
                            Player player = (Player) Objects.requireNonNull(arguments.get("target"));
                            open(player);
                        })
                );
    }

}
