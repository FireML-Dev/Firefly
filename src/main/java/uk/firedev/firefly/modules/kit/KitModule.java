package uk.firedev.firefly.modules.kit;

import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.kit.command.KitCommand;
import uk.firedev.firefly.placeholders.Placeholders;

import java.util.Map;
import java.util.TreeMap;

public class KitModule implements Module, Listener {

    private static KitModule instance = null;

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
        return ModuleConfig.getInstance().isModuleEnabled("kits");
    }

    @Override
    public void init() {
        loadKits();
        new KitRewardType().register();
        new KitCommand().initCommand();
    }

    @Override
    public void reload() {
        KitConfig.getInstance().reload();
        loadKits();
    }

    @Override
    public void unload() {}

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider ->
            provider.addAudienceDynamicPlaceholder("kit_available", (audience, value) -> {
                if (!isConfigEnabled()) {
                    return MessageConfig.getInstance().getFeatureDisabledMessage().toSingleMessage().get();
                }
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                Kit kit = getKit(value);
                if (kit == null) {
                    return Component.text(value + " is not a valid kit.");
                }
                boolean available = kit.hasPermission(player) && !kit.isOnCooldown(player.getUniqueId());
                return Component.text(available);
            }));
    }

    public NamespacedKey getKitKey() {
        return new NamespacedKey(Firefly.getInstance(), "kit");
    }

    public boolean isKit(ItemStack item) {
        if (!isConfigEnabled()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(getKitKey());
    }

    public @Nullable Kit getKit(@NotNull String name) {
        if (!isConfigEnabled()) {
            return null;
        }
        return loadedKits.get(name);
    }

    public @Nullable Kit getKit(ItemStack item) {
        if (!isConfigEnabled()) {
            return null;
        }
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
        return isConfigEnabled() ? Map.of() : Map.copyOf(loadedKits);
    }

    private void loadKits() {
        loadedKits.clear();
        if (!isConfigEnabled()) {
            return;
        }
        KitConfig.getInstance().getKitConfigs().forEach(section -> {
            String name = section.getName();
            try {
                Kit kit = new Kit(section);
                loadedKits.put(kit.getName(), kit);
            } catch (InvalidConfigurationException exception) {
                Loggers.warn(Firefly.getInstance().getComponentLogger(), "Kit " + name + " is not configured properly!");
            }
        });
    }

    // Interact Listener

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isConfigEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        Kit kit = getKit(item);
        if (kit == null) {
            return;
        }
        event.setCancelled(true);
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (kit.permissionOpen() && !kit.hasPermission(player)) {
            return;
        }
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItem(EquipmentSlot.HAND, item);
        kit.processRewards(player);
    }

}
