package uk.firedev.skylight.chat.titles.gui;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.firedev.daisylib.builders.ItemBuilder;
import uk.firedev.daisylib.libs.themoep.inventorygui.DynamicGuiElement;
import uk.firedev.daisylib.libs.themoep.inventorygui.GuiElementGroup;
import uk.firedev.daisylib.libs.themoep.inventorygui.InventoryGui;
import uk.firedev.daisylib.libs.themoep.inventorygui.StaticGuiElement;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.chat.titles.TitleManager;
import uk.firedev.skylight.config.GUIConfig;

import java.util.List;

public class PrefixGUI implements GUIBase {

    private final InventoryGui gui;
    private final Player player;

    public PrefixGUI(Player player) {
        FileConfiguration config = GUIConfig.getInstance().getConfig();
        List<String> setupTemp = config.getStringList("gui.prefixes.format");
        if (setupTemp.isEmpty()) {
            setupTemp = List.of(
                    "         ",
                    "  i   s  ",
                    "    x    "
            );
        }
        String[] setup = setupTemp.toArray(String[]::new);
        this.player = player;
        gui = new InventoryGui(Skylight.getInstance(), config.getString("gui.prefixes.title", "Titles"), setup);
        gui.setCloseAction(close -> false);
        gui.setFiller(getFiller(config.getString("gui.prefixes.filler")));

        DynamicGuiElement prefixesGroup = new DynamicGuiElement('i', who -> {
            GuiElementGroup group = new GuiElementGroup('i');
            TitleManager.getInstance().getPrefixes().stream()
                    .filter(prefix -> player.hasPermission(prefix.getPermission()))
                    .forEach(prefix -> group.addElement(
                            new StaticGuiElement('i', prefix.generateIcon(), click -> {
                                prefix.apply(player);
                                gui.close();
                                return true;
                            })
                    ));
            return group;
        });

        gui.addElement(prefixesGroup);

        gui.addElement(
                new StaticGuiElement('x', GUIConfig.getInstance().getExitButton(), click -> {
                    gui.close();
                    return true;
                })
        );

        gui.addElement(
                new StaticGuiElement('r', getRemoveButton(config), click -> {
                    TitleManager.getInstance().removePlayerPrefix(player);
                    gui.close();
                    return true;
                })
        );

    }

    public void open() {
        gui.show(player);
    }

    private ItemStack getRemoveButton(FileConfiguration config) {
        Material material = Material.FLINT_AND_STEEL;
        try {
            material = Material.valueOf(config.getString("gui.prefixes.remove.material", ""));
        } catch (IllegalArgumentException ignored) {}
        return new ItemBuilder(material)
                .withStringDisplay(config.getString("gui.prefixes.remove.display", "<yellow>Remove Prefix</yellow>"))
                .withStringLore(config.getStringList("gui.prefixes.remove.lore"))
                .build();
    }
    

}
