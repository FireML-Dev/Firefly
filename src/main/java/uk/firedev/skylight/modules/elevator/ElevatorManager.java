package uk.firedev.skylight.modules.elevator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.builders.ItemBuilder;
import uk.firedev.daisylib.crafting.ShapedRecipe;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.modules.elevator.command.ElevatorCommand;

import java.util.ArrayList;
import java.util.List;

public class ElevatorManager {

    private static ElevatorManager instance;
    private final Skylight plugin;
    private boolean loaded = false;

    private ElevatorManager() {
        plugin = Skylight.getInstance();
    }

    public static ElevatorManager getInstance() {
        if (instance == null) {
            instance = new ElevatorManager();
        }
        return instance;
    }

    public void load() {
        if (isLoaded()) {
            return;
        }
        PluginManager pm = this.plugin.getServer().getPluginManager();
        pm.registerEvents(new ElevatorListener(), this.plugin);
        ElevatorConfig.getInstance().reload();
        ElevatorCommand.getInstance().register();
        registerRecipe();
        loaded = true;
    }

    public boolean isLoaded() { return loaded; }

    public void reload() {
        if (!loaded) {
            return;
        }
        ElevatorConfig.getInstance().reload();
    }

    public void teleportPlayer(Player player, Elevator elevator) {
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
        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                elevator.handleBossBar(player);
            } else {
                ElevatorConfig.getInstance().getTeleportFailMessage().sendMessage(player);
            }
        });
    }

    public NamespacedKey getItemKey() {
        return ObjectUtils.createNamespacedKey("elevator-block", Skylight.getInstance());
    }

    public boolean isElevatorBlock(ItemStack itemStack) {
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(getItemKey(), PersistentDataType.BOOLEAN, false);
    }

    public ItemStack getElevatorBlock() {
        FileConfiguration config = ElevatorConfig.getInstance().getConfig();
        String material = config.getString("item.material", "IRON_BLOCK");
        ItemStack item = new ItemBuilder(material, Material.IRON_BLOCK)
                .withStringDisplay(config.getString("item.display", "<aqua>Elevator Block</aqua>"), null)
                .withStringLore(config.getStringList("item.lore"), null)
                .addEnchantment(Enchantment.UNBREAKING, 10)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .build();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(getItemKey(), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }
    
    private void registerRecipe() {
        List<ItemStack> stackList = new ArrayList<>();
        ElevatorConfig.getInstance().getConfig().getStringList("item.recipe").forEach(itemName ->
                stackList.add(new ItemStack(ItemUtils.getMaterial(itemName, Material.AIR)))
        );
        ShapedRecipe recipe = new ShapedRecipe(getItemKey(), getElevatorBlock(), stackList);
        if (recipe.register()) {
            Loggers.info(Skylight.getInstance().getComponentLogger(), "Registered Elevator Recipe");
        } else {
            Loggers.warn(Skylight.getInstance().getComponentLogger(), "Elevator Recipe failed to register.");
        }
    }

    public void migrateElevator(Block from, Block to) {
        Elevator fromElevator = new Elevator(from);
        Elevator toElevator = new Elevator(to);
        if (fromElevator.isElevator()) {
            fromElevator.setElevator(false);
            toElevator.setElevator(true);
        }
    }

}
