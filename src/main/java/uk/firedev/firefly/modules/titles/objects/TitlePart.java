package uk.firedev.firefly.modules.titles.objects;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.util.Utils;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

public interface TitlePart {

    void apply(@NonNull Player player);

    @NonNull ComponentSingleMessage getDisplay();
    @NonNull String getPermission();
    @NonNull ConfigurationSection getSection();
    boolean isPrefix();
    boolean isSuffix();

    @SuppressWarnings("UnstableApiUsage")
    default ItemStack generateIcon() {
        ItemType itemType = Utils.getItemType(getSection().getString("icon.material"), ItemType.NAME_TAG);
        ItemStack item = itemType.createItemStack();
        item.editMeta(meta -> {
            Component display = getDisplay().get();
            String displayString = getSection().getString("icon.display");
            if (displayString != null) {
                display = ComponentMessage.componentMessage(displayString).replace("{display}", getDisplay()).get();
            }
            meta.lore(ComponentMessage.componentMessage(getSection().getStringList("icon.lore")).get());
            meta.displayName(display);
        });
        return item;
    }

}
