package uk.firedev.firefly.modules.titles.gui;

import uk.firedev.daisylib.libs.triumphgui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.gui.PaginatedConfigGui;
import uk.firedev.firefly.modules.titles.TitleConfig;
import uk.firedev.firefly.modules.titles.TitleModule;

public class PrefixGui extends PaginatedConfigGui {

    public PrefixGui(@NonNull Player player) {
        super(
            TitleConfig.getInstance().getConfig().getConfigurationSection("gui.prefix"),
            player
        );
        addAction("remove-prefix", event -> {
            TitleModule.getInstance().removePlayerPrefix(player);
            player.closeInventory();
        });
        TitleModule.getInstance().getPrefixes().stream()
            .filter(prefix -> player.hasPermission(prefix.getPermission()))
            .forEach(prefix -> {
                GuiItem item = new GuiItem(prefix.generateIcon(), event -> {
                    prefix.apply(player);
                    player.closeInventory();
                });
                getGui().addItem(item);
            });
    }

}
