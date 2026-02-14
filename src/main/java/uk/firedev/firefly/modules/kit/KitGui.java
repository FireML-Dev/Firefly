package uk.firedev.firefly.modules.kit;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.gui.PaginatedConfigGui;
import uk.firedev.daisylib.libs.triumphgui.guis.GuiItem;

public class KitGui extends PaginatedConfigGui {

    public KitGui(@NonNull Player player) {
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
                    kit.giveToPlayerWithCooldown(player, null);
                    player.closeInventory();
                });
                getGui().addItem(item);
            });
    }

}
