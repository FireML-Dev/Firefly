package uk.firedev.firefly.modules.kit;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Manager;
import uk.firedev.firefly.modules.kit.command.KitCommand;

import java.util.List;

public class KitManager implements Manager {

    private static KitManager instance = null;

    private boolean loaded = false;

    public static KitManager getInstance() {
        if (instance == null) {
            instance = new KitManager();
        }
        return instance;
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new KitListener(), Firefly.getInstance());
        KitConfig.getInstance().reload();
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Kit Commands");
        KitCommand.getInstance().register(Firefly.getInstance());
        new KitRewardType().register();
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        KitConfig.getInstance().reload();
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        loaded = false;
    }

    @Override
    public boolean isLoaded() { return loaded; }

    public NamespacedKey getKitKey() {
        return ObjectUtils.createNamespacedKey("kit", Firefly.getInstance());
    }

    public boolean isKit(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(getKitKey());
    }

    public Kit getKit(ItemStack item) {
        if (!isKit(item)) {
            return null;
        }

        Section section = null;
        String kitName = item.getItemMeta().getPersistentDataContainer().get(getKitKey(), PersistentDataType.STRING);
        if (kitName != null) {
            section = KitConfig.getInstance().getConfig().getSection(kitName);
        }

        if (section == null) {
            return null;
        }
        return new Kit(section);
    }

    public List<Kit> getKits() {
        return KitConfig.getInstance().getKitConfigs().stream()
                .map(Kit::new)
                .toList();
    }

}
