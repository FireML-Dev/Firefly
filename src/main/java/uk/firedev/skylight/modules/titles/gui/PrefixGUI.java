package uk.firedev.skylight.modules.titles.gui;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.firedev.daisylib.builders.ItemBuilder;
import uk.firedev.daisylib.libs.themoep.inventorygui.*;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.config.GUIConfig;
import uk.firedev.skylight.modules.titles.TitleManager;

import java.util.List;

public class PrefixGUI {

    private final InventoryGui gui;
    private final Player player;

    public PrefixGUI(Player player) {
        FileConfiguration config = GUIConfig.getInstance().getConfig();
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
        gui = new InventoryGui(Skylight.getInstance(), config.getString("gui.prefixes.title", "Titles"), setup);
        gui.setCloseAction(close -> false);
        gui.setFiller(new ItemBuilder(config.getString("gui.prefixes.filler", ""), Material.GRAY_STAINED_GLASS_PANE)
                .withStringDisplay("", null)
                .build()
        );

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
            material = Material.valueOf(config.getString("gui.prefixes.remove.material", ""));
        } catch (IllegalArgumentException ignored) {}
        return new ItemBuilder(material)
                .withStringDisplay(config.getString("gui.prefixes.remove.display", "<yellow>Remove Prefix</yellow>"), null)
                .withStringLore(config.getStringList("gui.prefixes.remove.lore"), null)
                .build();
    }
    

}
