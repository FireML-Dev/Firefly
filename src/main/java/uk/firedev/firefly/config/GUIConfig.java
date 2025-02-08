package uk.firedev.firefly.config;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.builders.ItemBuilder;
import uk.firedev.daisylib.config.ConfigBase;
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

}
