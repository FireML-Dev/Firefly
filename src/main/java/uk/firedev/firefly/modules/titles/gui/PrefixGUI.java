package uk.firedev.firefly.modules.titles.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.firedev.daisylib.builders.ItemBuilder;
import uk.firedev.daisylib.libs.boostedyaml.YamlDocument;
import uk.firedev.daisylib.libs.themoep.inventorygui.DynamicGuiElement;
import uk.firedev.daisylib.libs.themoep.inventorygui.GuiElementGroup;
import uk.firedev.daisylib.libs.themoep.inventorygui.InventoryGui;
import uk.firedev.daisylib.libs.themoep.inventorygui.StaticGuiElement;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.GUIConfig;
import uk.firedev.firefly.modules.titles.TitleModule;

import java.util.List;

public class PrefixGUI {

    private final InventoryGui gui;
    private final Player player;

    public PrefixGUI(Player player) {
        YamlDocument config = GUIConfig.getInstance().getConfig();
        List<String> setupTemp = config.getStringList("gui.prefixes.format");
        if (setupTemp.isEmpty()) {
            setupTemp = List.of(
                "         ",
                " iiiiiii ",
                " iiiiiii ",
                " iiiiiii ",
                "fp x r nl"
            );
        }
        String[] setup = setupTemp.toArray(String[]::new);
        this.player = player;
        gui = new InventoryGui(Firefly.getInstance(), config.getString("gui.prefixes.title", "Titles"), setup);
        gui.setCloseAction(close -> false);
        gui.setFiller(ItemBuilder.itemBuilder(config.getString("gui.prefixes.filler", ""), Material.GRAY_STAINED_GLASS_PANE)
                .withStringDisplay("", null)
                .getItem()
        );

        DynamicGuiElement prefixesGroup = new DynamicGuiElement('i', who -> {
            GuiElementGroup group = new GuiElementGroup('i');
            TitleModule.getInstance().getPrefixes().stream()
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
                    TitleModule.getInstance().removePlayerPrefix(player);
                    gui.close();
                    return true;
                })
        );

        GUIConfig.getInstance().addAllPageElements(gui);

    }

    public void open() {
        gui.show(player);
    }

    private ItemStack getRemoveButton(YamlDocument config) {
        Material material = Material.FLINT_AND_STEEL;
        try {
            material = Material.valueOf(config.getString("gui.prefixes.remove.material", ""));
        } catch (IllegalArgumentException ignored) {}
        return ItemBuilder.itemBuilder(material)
                .withStringDisplay(config.getString("gui.prefixes.remove.display", "<yellow>Remove Prefix</yellow>"), null)
                .withStringLore(config.getStringList("gui.prefixes.remove.lore"), null)
                .getItem();
    }
    

}
