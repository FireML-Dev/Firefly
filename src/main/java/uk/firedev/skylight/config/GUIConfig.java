package uk.firedev.skylight.config;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import uk.firedev.daisylib.builders.ItemBuilder;
import uk.firedev.skylight.Skylight;

public class GUIConfig extends uk.firedev.daisylib.Config {

    private static GUIConfig instance = null;

    private GUIConfig() {
        super("gui.yml", Skylight.getInstance(), true, true);
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
        return new ItemBuilder(material)
                .withStringDisplay(getConfig().getString("gui.global.exit.display", "<red>Exit</red>"), null)
                .withStringLore(getConfig().getStringList("gui.global.exit.lore"), null)
                .build();
    }

    public ItemStack getNextPageButton() {
        Material material = Material.PAPER;
        try {
            material = Material.valueOf(getConfig().getString("gui.global.next-page.material"));
        } catch (IllegalArgumentException ignored) {}
        return new ItemBuilder(material)
                .withStringDisplay(getConfig().getString("gui.global.next-page.display", "<aqua>Next Page</aqua>"), null)
                .withStringLore(getConfig().getStringList("gui.global.next-page.lore"), null)
                .build();
    }

    public ItemStack getPreviousPageButton() {
        Material material = Material.PAPER;
        try {
            material = Material.valueOf(getConfig().getString("gui.global.previous-page.material"));
        } catch (IllegalArgumentException ignored) {}
        return new ItemBuilder(material)
                .withStringDisplay(getConfig().getString("gui.global.previous-page.display", "<aqua>Previous Page</aqua>"), null)
                .withStringLore(getConfig().getStringList("gui.global.previous-page.lore"), null)
                .build();
    }

    public ItemStack getFirstPageButton() {
        Material material = Material.ARROW;
        try {
            material = Material.valueOf(getConfig().getString("gui.global.first-page.material"));
        } catch (IllegalArgumentException ignored) {}
        return new ItemBuilder(material)
                .withStringDisplay(getConfig().getString("gui.global.first-page.display", "<aqua>First Page</aqua>"), null)
                .withStringLore(getConfig().getStringList("gui.global.first-page.lore"), null)
                .build();
    }

    public ItemStack getLastPageButton() {
        Material material = Material.ARROW;
        try {
            material = Material.valueOf(getConfig().getString("gui.global.last-page.material"));
        } catch (IllegalArgumentException ignored) {}
        return new ItemBuilder(material)
                .withStringDisplay(getConfig().getString("gui.global.last-page.display", "<aqua>Last Page</aqua>"), null)
                .withStringLore(getConfig().getStringList("gui.global.last-page.lore"), null)
                .build();
    }

}
