package uk.firedev.firefly.modules.kit;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.gui.PaginatedConfigGui;
import uk.firedev.daisylib.libs.triumphgui.guis.GuiItem;

public class KitGui extends PaginatedConfigGui {

    public KitGui(@NotNull Player player) {
        super(
            KitConfig.getInstance().getConfig().getConfigurationSection("gui"),
            player
        );
        KitModule.getInstance().getKits().values()
            .forEach(kit -> {
                if (!kit.isPlayerVisible()) {
                    return;
                }
                GuiItem item = new GuiItem(kit.buildItem(), click -> {
                    kit.awardKit(player, false);
                    player.closeInventory();
                });
                getGui().addItem(item);
            });
    }

}
