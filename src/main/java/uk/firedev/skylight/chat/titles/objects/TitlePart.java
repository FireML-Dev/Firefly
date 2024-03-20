package uk.firedev.skylight.chat.titles.objects;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.utils.ComponentUtils;

import java.util.Map;

public interface TitlePart {

    void apply(@NotNull Player player);

    @NotNull Component getDisplay();
    @NotNull String getPermission();
    @NotNull ConfigurationSection getConfigurationSection();
    boolean isPrefix();
    boolean isSuffix();

    default ItemStack generateIcon() {
        Material material = Material.NAME_TAG;
        try {
            String matName = getConfigurationSection().getString("icon.material");
            if (matName != null) {
                material = Material.valueOf(matName.toUpperCase());
            }
        } catch (IllegalArgumentException ignored) {}
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        Component display = getDisplay();
        String displayString = getConfigurationSection().getString("icon.display");
        if (displayString != null) {
            display = ComponentUtils.parseComponent(displayString);
            display = ComponentUtils.parsePlaceholders(display,
                    Map.of("display", getDisplay())
            );
        }
        meta.lore(ComponentUtils.parseComponentList(getConfigurationSection().getStringList("icon.lore")));
        meta.displayName(display);
        item.setItemMeta(meta);
        return item;
    }

}
