package uk.firedev.skylight.chat.titles.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import uk.firedev.daisylib.builders.ItemBuilder;

public interface GUIBase {

    default ItemStack getFiller(String materialName) {
        Material material = Material.GRAY_STAINED_GLASS_PANE;
        try {
            material = Material.valueOf(materialName);
        } catch (IllegalArgumentException ignored) {}
        return new ItemBuilder(material)
                .withStringDisplay("")
                .build();
    }

}
