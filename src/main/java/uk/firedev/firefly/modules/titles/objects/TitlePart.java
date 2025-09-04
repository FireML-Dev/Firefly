package uk.firedev.firefly.modules.titles.objects;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.messagelib.message.ComponentMessage;
import uk.firedev.messagelib.message.ComponentSingleMessage;

public interface TitlePart {

    void apply(@NotNull Player player);

    @NotNull ComponentSingleMessage getDisplay();
    @NotNull String getPermission();
    @NotNull ConfigurationSection getSection();
    boolean isPrefix();
    boolean isSuffix();

    default ItemStack generateIcon() {
        ItemType itemType = ItemUtils.getItemType(getSection().getString("icon.material"), ItemType.NAME_TAG);
        ItemStack item = itemType.createItemStack();
        item.editMeta(meta -> {
            Component display = getDisplay().get();
            String displayString = getSection().getString("icon.display");
            if (displayString != null) {
                display = ComponentMessage.componentMessage(displayString).replace("display", getDisplay()).get();
            }
            meta.lore(ComponentMessage.componentMessage(getSection().getStringList("icon.lore")).get());
            meta.displayName(display);
        });
        return item;
    }

}
