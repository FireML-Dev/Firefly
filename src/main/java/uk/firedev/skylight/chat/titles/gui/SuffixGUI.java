package uk.firedev.skylight.chat.titles.gui;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.firedev.daisylib.builders.ItemBuilder;
import uk.firedev.daisylib.libs.themoep.inventorygui.*;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.chat.titles.TitleManager;
import uk.firedev.skylight.config.GUIConfig;

import java.util.List;

public class SuffixGUI {

    private final InventoryGui gui;
    private final Player player;

    public SuffixGUI(Player player) {
        FileConfiguration config = GUIConfig.getInstance().getConfig();
        List<String> setupTemp = config.getStringList("gui.suffixes.format");
        if (setupTemp.isEmpty()) {
            setupTemp = List.of(
                    "         ",
                    "  i   s  ",
                    "    x    "
            );
        }
        String[] setup = setupTemp.toArray(String[]::new);
        this.player = player;
        gui = new InventoryGui(Skylight.getInstance(), config.getString("gui.suffixes.title", "Titles"), setup);
        gui.setCloseAction(close -> false);
        gui.setFiller(new ItemBuilder(config.getString("gui.suffixes.filler", ""), Material.GRAY_STAINED_GLASS_PANE)
                .withStringDisplay("")
                .build()
        );

        DynamicGuiElement suffixesGroup = new DynamicGuiElement('s', who -> {
            GuiElementGroup group = new GuiElementGroup('s');
            TitleManager.getInstance().getSuffixes().stream()
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
                    TitleManager.getInstance().removePlayerSuffix(player);
                    gui.close();
                    return true;
                })
        );

        gui.addElement(new GuiPageElement('f',
                GUIConfig.getInstance().getFirstPageButton(),
                GuiPageElement.PageAction.FIRST)
        );

        gui.addElement(new GuiPageElement('p',
                GUIConfig.getInstance().getPreviousPageButton(),
                GuiPageElement.PageAction.PREVIOUS)
        );

        gui.addElement(new GuiPageElement('n',
                GUIConfig.getInstance().getNextPageButton(),
                GuiPageElement.PageAction.NEXT)
        );

        gui.addElement(new GuiPageElement('l',
                GUIConfig.getInstance().getLastPageButton(),
                GuiPageElement.PageAction.LAST)
        );

    }

    public void open() {
        gui.show(player);
    }

    private ItemStack getRemoveButton(FileConfiguration config) {
        Material material = Material.FLINT_AND_STEEL;
        try {
            material = Material.valueOf(config.getString("gui.suffixes.remove.material", ""));
        } catch (IllegalArgumentException ignored) {}
        return new ItemBuilder(material)
                .withStringDisplay(config.getString("gui.suffixes.remove.display", "<yellow>Remove Suffix</yellow>"))
                .withStringLore(config.getStringList("gui.suffixes.remove.lore"))
                .build();
    }

}
