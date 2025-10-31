package uk.firedev.firefly.modules.command.commands.workstations;

import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class WorkbenchCommand implements WorkstationCommand {

    @NotNull
    @Override
    public String getConfigName() {
        return "workbench";
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(
            MenuType.CRAFTING.create(player)
        );
    }

}
