package uk.firedev.firefly.modules.kit;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.gui.GUIUtils;
import uk.firedev.daisylib.libs.themoep.inventorygui.DynamicGuiElement;
import uk.firedev.daisylib.libs.themoep.inventorygui.GuiElementGroup;
import uk.firedev.daisylib.libs.themoep.inventorygui.InventoryGui;
import uk.firedev.daisylib.libs.themoep.inventorygui.StaticGuiElement;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.GUIConfig;

import java.util.List;

public class KitGUI {

    private final @NotNull InventoryGui gui;
    private final @NotNull Player player;

    public KitGUI(@NotNull Player player) {
        YamlConfiguration config = GUIConfig.getInstance().getConfig();
        List<String> setupTemp = config.getStringList("gui.kits.format");
        if (setupTemp.isEmpty()) {
            setupTemp = List.of(
                    "         ",
                    " kkkkkkk ",
                    " kkkkkkk ",
                    " kkkkkkk ",
                    "fp  x  nl"
            );
        }
        String[] setup = setupTemp.toArray(String[]::new);
        this.player = player;
        gui = new InventoryGui(
                Firefly.getInstance(),
                GUIUtils.getFormattedTitle(config.getString("gui.kits.title", "Kits"), null),
                setup
        );
        gui.setCloseAction(close -> false);
        gui.setFiller(GUIUtils.createItemStack(
                config.getString("gui.kits.filler", "gray_stained_glass_pane"),
                Material.GRAY_STAINED_GLASS_PANE,
                "",
                List.of()
        ));
        addKitElements();
        GUIConfig.getInstance().addAllPageElements(gui);

        // Exit button
        gui.addElement(
            new StaticGuiElement('x', GUIConfig.getInstance().getExitButton(), click -> {
                gui.close();
                return true;
            })
        );

    }

    private void addKitElements() {
        DynamicGuiElement dynamicGroup = new DynamicGuiElement('k', human -> {
            GuiElementGroup group = new GuiElementGroup('k');
            KitModule.getInstance().getKits().values()
                    .forEach(kit -> {
                        if (!kit.isPlayerVisible()) {
                            return;
                        }
                        group.addElement(new StaticGuiElement('k', kit.buildItem(), click -> {
                            if (!(human instanceof Player humanPlayer)) {
                                return true;
                            }
                            kit.awardKit(humanPlayer, false);
                            return true;
                        }));
                    });
            return group;
        });
        gui.addElement(dynamicGroup);
    }

    public void open() {
        gui.show(player);
    }

}
