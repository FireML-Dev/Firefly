package uk.firedev.firefly.config;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.builders.ItemBuilder;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.libs.themoep.inventorygui.GuiPageElement;
import uk.firedev.daisylib.libs.themoep.inventorygui.InventoryGui;
import uk.firedev.firefly.Firefly;

public class GUIConfig extends ConfigBase {

    private static GUIConfig instance = null;

    private GUIConfig() {
        super("gui.yml", "gui.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
    }

    public static GUIConfig getInstance() {
        if (instance == null) {
            instance = new GUIConfig();
        }
        return instance;
    }

    public ItemStack getExitButton() {
        Material material = Material.STRUCTURE_VOID;
        try {
            material = Material.valueOf(getConfig().getString("gui.global.exit.material"));
        } catch (IllegalArgumentException ignored) {}
        return ItemBuilder.create(material)
                .withStringDisplay(getConfig().getString("gui.global.exit.display", "<red>Exit</red>"), null)
                .withStringLore(getConfig().getStringList("gui.global.exit.lore"), null)
                .getItem();
    }

    public GuiPageElement getNextPageButton() {
        Material material = Material.PAPER;
        try {
            material = Material.valueOf(getConfig().getString("gui.global.next-page.material"));
        } catch (IllegalArgumentException ignored) {}
        ItemStack item = ItemBuilder.create(material)
                .withStringDisplay(getConfig().getString("gui.global.next-page.display", "<aqua>Next Page</aqua>"), null)
                .withStringLore(getConfig().getStringList("gui.global.next-page.lore"), null)
                .getItem();
        return new GuiPageElement('n', item, GuiPageElement.PageAction.NEXT);
    }

    public GuiPageElement getPreviousPageButton() {
        Material material = Material.PAPER;
        try {
            material = Material.valueOf(getConfig().getString("gui.global.previous-page.material"));
        } catch (IllegalArgumentException ignored) {}
        ItemStack item = ItemBuilder.create(material)
                .withStringDisplay(getConfig().getString("gui.global.previous-page.display", "<aqua>Previous Page</aqua>"), null)
                .withStringLore(getConfig().getStringList("gui.global.previous-page.lore"), null)
                .getItem();
        return new GuiPageElement('p', item, GuiPageElement.PageAction.PREVIOUS);
    }

    public GuiPageElement getFirstPageButton() {
        Material material = Material.ARROW;
        try {
            material = Material.valueOf(getConfig().getString("gui.global.first-page.material"));
        } catch (IllegalArgumentException ignored) {}
        ItemStack item = ItemBuilder.create(material)
                .withStringDisplay(getConfig().getString("gui.global.first-page.display", "<aqua>First Page</aqua>"), null)
                .withStringLore(getConfig().getStringList("gui.global.first-page.lore"), null)
                .getItem();
        return new GuiPageElement('f', item, GuiPageElement.PageAction.FIRST);
    }

    public GuiPageElement getLastPageButton() {
        Material material = Material.ARROW;
        try {
            material = Material.valueOf(getConfig().getString("gui.global.last-page.material"));
        } catch (IllegalArgumentException ignored) {}
        ItemStack item = ItemBuilder.create(material)
                .withStringDisplay(getConfig().getString("gui.global.last-page.display", "<aqua>Last Page</aqua>"), null)
                .withStringLore(getConfig().getStringList("gui.global.last-page.lore"), null)
                .getItem();
        return new GuiPageElement('l', item, GuiPageElement.PageAction.LAST);
    }

    public void addAllPageElements(@NotNull InventoryGui gui) {
        gui.addElements(getFirstPageButton(), getPreviousPageButton(), getNextPageButton(), getLastPageButton());
    }

}
