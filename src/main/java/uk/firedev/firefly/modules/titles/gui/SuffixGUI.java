package uk.firedev.firefly.modules.titles.gui;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.firedev.daisylib.api.builders.ItemBuilder;
import uk.firedev.daisylib.libs.themoep.inventorygui.DynamicGuiElement;
import uk.firedev.daisylib.libs.themoep.inventorygui.GuiElementGroup;
import uk.firedev.daisylib.libs.themoep.inventorygui.InventoryGui;
import uk.firedev.daisylib.libs.themoep.inventorygui.StaticGuiElement;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.GUIConfig;
import uk.firedev.firefly.modules.titles.TitleModule;

import java.util.List;

public class SuffixGUI {

    private final InventoryGui gui;
    private final Player player;

    public SuffixGUI(Player player) {
        YamlConfiguration config = GUIConfig.getInstance().getConfig();
        List<String> setupTemp = config.getStringList("gui.suffixes.format");
        if (setupTemp.isEmpty()) {
            setupTemp = List.of(
                "         ",
                " sssssss ",
                " sssssss ",
                " sssssss ",
                "fp x r nl"
            );
        }
        String[] setup = setupTemp.toArray(String[]::new);
        this.player = player;
        gui = new InventoryGui(Firefly.getInstance(), config.getString("gui.suffixes.title", "Titles"), setup);
        gui.setCloseAction(close -> false);
        gui.setFiller(ItemBuilder.create(config.getString("gui.suffixes.filler", ""), Material.GRAY_STAINED_GLASS_PANE)
                .withStringDisplay("", null)
                .getItem()
        );

        DynamicGuiElement suffixesGroup = new DynamicGuiElement('s', who -> {
            GuiElementGroup group = new GuiElementGroup('s');
            TitleModule.getInstance().getSuffixes().stream()
                    .filter(suffix -> player.hasPermission(suffix.getPermission()))
                    .forEach(suffix -> group.addElement(
                            new StaticGuiElement('s', suffix.generateIcon(), click -> {
                                suffix.apply(player);
                                gui.close();
                                return true;
                            })
                    ));
            return group;
        });

        gui.addElement(suffixesGroup);

        gui.addElement(
                new StaticGuiElement('x', GUIConfig.getInstance().getExitButton(), click -> {
                    gui.close();
                    return true;
                })
        );

        gui.addElement(
                new StaticGuiElement('r', getRemoveButton(config), click -> {
                    TitleModule.getInstance().removePlayerSuffix(player);
                    gui.close();
                    return true;
                })
        );

        GUIConfig.getInstance().addAllPageElements(gui);

    }

    public void open() {
        gui.show(player);
    }

    private ItemStack getRemoveButton(YamlConfiguration config) {
        Material material = Material.FLINT_AND_STEEL;
        try {
            material = Material.valueOf(config.getString("gui.suffixes.remove.material", ""));
        } catch (IllegalArgumentException ignored) {}
        return ItemBuilder.create(material)
                .withStringDisplay(config.getString("gui.suffixes.remove.display", "<yellow>Remove Suffix</yellow>"), null)
                .withStringLore(config.getStringList("gui.suffixes.remove.lore"), null)
                .getItem();
    }

}
