package uk.firedev.firefly.modules.command.commands.menus;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.modules.command.Command;

import java.util.List;
import java.util.Objects;

public class GrindstoneCommand extends Command {

    private final CommandTree command = new CommandTree(getName())
            .withPermission(getPermission())
            .executesPlayer(info -> {
                open(info.sender());
            })
            .then(new EntitySelectorArgument.OnePlayer("target")
                    .executes((sender, arguments) -> {
                        Player player = (Player) Objects.requireNonNull(arguments.get("target"));
                        open(player);
                    })
            );

    private void open(@NotNull Player player) {
        player.openInventory(
                MenuType.GRINDSTONE.create(player, Component.empty())
        );
    }

    @Override
    public String getName() {
        return "grindstone";
    }

    @Override
    public CommandTree getCommand() {
        return command;
    }

}
