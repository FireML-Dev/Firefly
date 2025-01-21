package uk.firedev.firefly.modules.command.commands.workstations;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.daisylib.libs.commandapi.arguments.EntitySelectorArgument;
import uk.firedev.firefly.modules.command.Command;

import java.util.Objects;

public class WorkbenchCommand extends Command {

    private final CommandTree command = new CommandTree(getName())
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

    private void open(@NotNull Player player) {
        player.openInventory(
                MenuType.CRAFTING.create(player, Component.empty())
        );
    }

    @Override
    public String getName() {
        return "workbench";
    }

    @Override
    public CommandTree getCommand() {
        return command;
    }

}
