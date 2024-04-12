package uk.firedev.skylight.modules.kit;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.skylight.Skylight;

import java.util.List;
import java.util.Objects;

public class KitManager extends uk.firedev.daisylib.Config {

    private static KitManager instance = null;

    private boolean loaded = false;
    private final Skylight plugin;

    private KitManager() {
        super("kits.yml", Skylight.getInstance(), false);
        plugin = Skylight.getInstance();
    }

    public static KitManager getInstance() {
        if (instance == null) {
            instance = new KitManager();
        }
        return instance;
    }

    public void load() {
        PluginManager pm = this.plugin.getServer().getPluginManager();
        pm.registerEvents(new KitListener(), plugin);
        AwardKitCommand.getInstance().register();
        loaded = true;
    }

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
        ConfigurationSection section = getConfig().getConfigurationSection(
                item.getItemMeta().getPersistentDataContainer().getOrDefault(getKitKey(), PersistentDataType.STRING, "")
        );
        if (section == null) {
            return null;
        }
        return new Kit(section);
    }

    public List<ConfigurationSection> getKitConfigs() {
        ConfigurationSection kitsSection = getConfig().getConfigurationSection("kits");
        if (kitsSection == null) {
            return List.of();
        }
        return kitsSection.getKeys(false).stream()
                .map(kitsSection::getConfigurationSection)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<Kit> getKits() {
        return getKitConfigs().stream()
                .map(Kit::new)
                .toList();
    }

}
