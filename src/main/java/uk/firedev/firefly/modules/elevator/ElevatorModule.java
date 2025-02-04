package uk.firedev.firefly.modules.elevator;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.builders.ItemBuilder;
import uk.firedev.daisylib.api.crafting.ShapedRecipe;
import uk.firedev.daisylib.api.utils.ItemUtils;
import uk.firedev.daisylib.api.utils.PlayerHelper;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.elevator.command.ElevatorCommand;
import uk.firedev.firefly.modules.teleportation.TeleportModule;
import uk.firedev.firefly.placeholders.Placeholders;

import java.util.ArrayList;
import java.util.List;

public class ElevatorModule implements Module {

    private static ElevatorModule instance;
    private final Firefly plugin;
    private boolean loaded = false;

    private ElevatorModule() {
        plugin = Firefly.getInstance();
    }

    public static ElevatorModule getInstance() {
        if (instance == null) {
            instance = new ElevatorModule();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "Elevator";
    }

    @Override
    public boolean isConfigEnabled() {
        return ModuleConfig.getInstance().elevatorModuleEnabled();
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        PluginManager pm = this.plugin.getServer().getPluginManager();
        pm.registerEvents(new ElevatorListener(), this.plugin);
        ElevatorConfig.getInstance().reload();
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering Elevator Command");
        ElevatorCommand.getCommand().register(Firefly.getInstance());
        registerRecipe();
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        ElevatorConfig.getInstance().reload();
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
            provider.addAudiencePlaceholder("elevator_level", audience -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                Block block = PlayerHelper.getPlayerStandingOn(player);
                Elevator elevator = new Elevator(block);
                if (elevator.isElevator()) {
                    return Component.text("N/A");
                }
                return Component.text(elevator.getCurrentPosition());
            });
        });
    }

    public void teleportPlayer(@NotNull Player player, Elevator elevator) {
        if (elevator == null || !elevator.isElevator()) {
            return;
        }
        Location location = elevator.getTPLocation();
        location.setYaw(player.getYaw());
        location.setPitch(player.getPitch());
        if (!location.getBlock().isPassable()) {
            ElevatorConfig.getInstance().getUnsafeLocationMessage().sendMessage(player);
            return;
        }
        boolean teleportManager = TeleportModule.getInstance().isLoaded();
        final Location lastLocation = teleportManager ? TeleportModule.getInstance().getLastLocation(player) : null;
        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                elevator.handleBossBar(player);
                if (teleportManager && lastLocation != null) {
                    TeleportModule.getInstance().setLastLocation(player, lastLocation);
                }
            } else {
                MessageConfig.getInstance().getErrorOccurredMessage().sendMessage(player);
            }
        });
    }

    public NamespacedKey getItemKey() {
        return new NamespacedKey(Firefly.getInstance(), "elevator-block");
    }

    public boolean isElevatorBlock(ItemStack itemStack) {
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(getItemKey(), PersistentDataType.BOOLEAN, false);
    }

    public ItemStack getElevatorBlock() {
        YamlConfiguration config = ElevatorConfig.getInstance().getConfig();
        String material = config.getString("item.material", "IRON_BLOCK");
        ItemStack item = ItemBuilder.create(material, Material.IRON_BLOCK)
                .withStringDisplay(config.getString("item.display", "<aqua>Elevator Block</aqua>"), null)
                .withStringLore(config.getStringList("item.lore"), null)
                .addEnchantment(Enchantment.UNBREAKING, 10)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .getItem();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(getItemKey(), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }
    
    private void registerRecipe() {
        List<ItemStack> stackList = new ArrayList<>();
        ElevatorConfig.getInstance().getConfig().getStringList("item.recipe").forEach(itemName ->
                stackList.add(ItemStack.of(ItemUtils.getMaterial(itemName, Material.AIR)))
        );
        ShapedRecipe recipe = new ShapedRecipe(Firefly.getInstance(), getItemKey(), getElevatorBlock(), stackList);
        if (recipe.register(true)) {
            Loggers.info(Firefly.getInstance().getComponentLogger(), "Registered Elevator Recipe");
        } else {
            Loggers.warn(Firefly.getInstance().getComponentLogger(), "Elevator Recipe failed to register.");
        }
    }

}
