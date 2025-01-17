package uk.firedev.firefly.modules.kit;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.daisylib.api.utils.ObjectUtils;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.kit.command.KitCommand;
import uk.firedev.firefly.placeholders.Placeholders;

import java.util.*;

public class KitModule implements Module {

    private static KitModule instance = null;

    private boolean loaded = false;
    private final TreeMap<String, Kit> loadedKits = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public static KitModule getInstance() {
        if (instance == null) {
            instance = new KitModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "Kit";
    }

    @Override
    public boolean isConfigEnabled() {
        return ModuleConfig.getInstance().kitsModuleEnabled();
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new KitListener(), Firefly.getInstance());
        KitConfig.getInstance().reload();
        loadKits();
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Kit Commands");
        KitCommand.getCommand().register(Firefly.getInstance());
        new KitRewardType().register();
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        KitConfig.getInstance().reload();
        loadKits();
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

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider -> {
            provider.addAudienceDynamicPlaceholder("kit_available", (audience, value) -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                Kit kit = getKit(value);
                if (kit == null) {
                    return Component.text(value + " is not a valid kit.");
                }
                return Component.text(!kit.isOnCooldown(player.getUniqueId()));
            });
        });
    }

    public NamespacedKey getKitKey() {
        return ObjectUtils.createNamespacedKey("kit", Firefly.getInstance());
    }

    public boolean isKit(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(getKitKey());
    }

    public @Nullable Kit getKit(@NotNull String name) {
        return loadedKits.get(name);
    }

    public Kit getKit(ItemStack item) {
        if (!isKit(item)) {
            return null;
        }
        String kitName = item.getItemMeta().getPersistentDataContainer().get(getKitKey(), PersistentDataType.STRING);
        if (kitName != null) {
            return getKit(kitName);
        }
        return null;
    }

    public Map<String, Kit> getKits() {
        return Map.copyOf(loadedKits);
    }

    private void loadKits() {
        loadedKits.clear();
        KitConfig.getInstance().getKitConfigs().forEach(section -> {
            String name = Objects.requireNonNull(section.getNameAsString());
            try {
                loadedKits.put(name, new Kit(section));
            } catch (InvalidConfigurationException exception) {
                Loggers.warn(Firefly.getInstance().getComponentLogger(), "Kit " + name + " is not configured properly!");
            }
        });
    }

}
