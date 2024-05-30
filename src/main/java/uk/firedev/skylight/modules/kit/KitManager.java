package uk.firedev.skylight.modules.kit;

import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.skylight.Manager;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.modules.kit.command.AwardKitCommand;

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
        pm.registerEvents(new KitListener(), Skylight.getInstance());
        KitConfig.getInstance().reload();
        AwardKitCommand.getInstance().register();
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
        CommandAPI.unregister(AwardKitCommand.getInstance().getName());
        loaded = false;
    }

    @Override
    public boolean isLoaded() { return loaded; }

    public NamespacedKey getKitKey() {
        return ObjectUtils.createNamespacedKey("kit", Skylight.getInstance());
    }

    public boolean isKit(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(getKitKey());
    }

    public Kit getKit(ItemStack item) {
        if (!isKit(item)) {
            return null;
        }

        ConfigurationSection section = null;
        String kitName = item.getItemMeta().getPersistentDataContainer().get(getKitKey(), PersistentDataType.STRING);
        if (kitName != null) {
            section = KitConfig.getInstance().getConfig().getConfigurationSection(kitName);
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
