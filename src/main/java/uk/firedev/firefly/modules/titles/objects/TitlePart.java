package uk.firedev.firefly.modules.titles.objects;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.api.utils.ItemUtils;

public interface TitlePart {

    void apply(@NotNull Player player);

    @NotNull ComponentMessage getDisplay();
    @NotNull String getPermission();
    @NotNull ConfigurationSection getSection();
    boolean isPrefix();
    boolean isSuffix();

    default ItemStack generateIcon() {
        ItemType itemType = ItemUtils.getItemType(getSection().getString("icon.material"), ItemType.NAME_TAG);
        ItemStack item = itemType.createItemStack();
        item.editMeta(meta -> {
            Component display = getDisplay().getMessage();
            String displayString = getSection().getString("icon.display");
            if (displayString != null) {
                display = ComponentMessage.fromString(displayString).replace("display", getDisplay().getMessage()).getMessage();
            }
            meta.lore(getSection().getStringList("icon.lore").stream().map(str -> ComponentMessage.fromString(str).getMessage()).toList());
            meta.displayName(display);
        });
        return item;
    }

}
