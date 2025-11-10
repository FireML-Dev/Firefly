package uk.firedev.firefly.modules.command.commands.workstations;

import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class LoomCommand implements WorkstationCommand {

    @NotNull
    @Override
    public String getConfigName() {
        return "loom";
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(
            MenuType.LOOM.create(player)
        );
    }

}
