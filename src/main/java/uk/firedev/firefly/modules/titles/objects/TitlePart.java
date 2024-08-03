package uk.firedev.firefly.modules.titles.objects;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.message.component.ComponentReplacer;

public interface TitlePart {

    void apply(@NotNull Player player);

    @NotNull Component getDisplay();
    @NotNull String getPermission();
    @NotNull Section getSection();
    boolean isPrefix();
    boolean isSuffix();

    default ItemStack generateIcon() {
        Material material = Material.NAME_TAG;
        try {
            String matName = getSection().getString("icon.material");
            if (matName != null) {
                material = Material.valueOf(matName.toUpperCase());
            }
        } catch (IllegalArgumentException ignored) {}
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        Component display = getDisplay();
        String displayString = getSection().getString("icon.display");
        if (displayString != null) {
            ComponentReplacer replacer = new ComponentReplacer().addReplacement("display", getDisplay());
            display = new ComponentMessage(displayString).applyReplacer(replacer).getMessage();
        }
        meta.lore(getSection().getStringList("icon.lore").stream().map(s -> new ComponentMessage(s).getMessage()).toList());
        meta.displayName(display);
        item.setItemMeta(meta);
        return item;
    }

}
