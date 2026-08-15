package uk.firedev.firefly.modules.titles.gui;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import uk.firedev.firefly.gui.PaginatedConfigGui;
import uk.firedev.firefly.modules.titles.TitleConfig;
import uk.firedev.firefly.modules.titles.TitleModule;

public class SuffixGui extends PaginatedConfigGui {

    public SuffixGui(@NonNull Player player) {
        super(
            TitleConfig.getInstance().getConfig().getConfigurationSection("gui.suffix"),
            player
        );
        addAction("remove-suffix", _ -> {
            TitleModule.getInstance().removePlayerSuffix(player);
            player.closeInventory();
        });
        TitleModule.getInstance().getSuffixes().stream()
            .filter(suffix -> player.hasPermission(suffix.getPermission()))
            .forEach(suffix -> {
                GuiItem item = new GuiItem(suffix.generateIcon(), event -> {
                    suffix.apply(player);
                    player.closeInventory();
                });
                getGui().addItem(item);
            });
    }

}
