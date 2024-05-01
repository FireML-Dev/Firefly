package uk.firedev.skylight.modules.elevator;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
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
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.config.MainConfig;
import uk.firedev.skylight.config.MessageConfig;

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
        PluginManager pm = this.plugin.getServer().getPluginManager();
        pm.registerEvents(new ElevatorListener(), this.plugin);
        ElevatorCommand.getInstance().register();
        // TODO uncomment when 1.20.6 is stable
        //registerRecipe();
        loaded = true;
    }

    public boolean isLoaded() { return loaded; }

    public void reload() {
        if (!loaded) {
            return;
        }
    }

    public Component getBossBarTitle(Elevator elevator) {
        ComponentReplacer replacer = new ComponentReplacer().addReplacements(
                "current", String.valueOf(elevator.getCurrentPosition() + 1),
                "all", String.valueOf(elevator.getStack().size())
        );
        return new ComponentMessage(MainConfig.getInstance().getConfig(), "elevator.bossbar.title", "<yellow>Floor {current} of {all}</yellow>").applyReplacer(replacer).getMessage();
    }

    public BossBar.Color getBossBarColor() {
        try {
            return BossBar.Color.valueOf(
                    MainConfig.getInstance().getConfig().getString(
                            "elevator.bossbar.color",
                            "RED"
                    ).toUpperCase()
            );
        } catch (IllegalArgumentException ex) {
            return BossBar.Color.RED;
        }
    }

    public BossBar.Overlay getBossBarOverlay() {
        try {
            return BossBar.Overlay.valueOf(
                    MainConfig.getInstance().getConfig().getString(
                            "elevator.bossbar.overlay",
                            "PROGRESS"
                    ).toUpperCase()
            );
        } catch (IllegalArgumentException ex) {
            return BossBar.Overlay.PROGRESS;
        }
    }

    public void teleportPlayer(Player player, Elevator elevator) {
        if (elevator == null || !elevator.isElevator()) {
            return;
        }
        Location location = elevator.getTPLocation();
        location.setYaw(player.getYaw());
        location.setPitch(player.getPitch());
        if (!location.getBlock().isPassable()) {
            MessageConfig.getInstance().getElevatorUnsafeLocationMessage().sendMessage(player);
            return;
        }
        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                elevator.handleBossBar(player);
            } else {
                MessageConfig.getInstance().getElevatorTeleportFailMessage().sendMessage(player);
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
        FileConfiguration config = MainConfig.getInstance().getConfig();
        Material material = Material.getMaterial(config.getString("elevator.item.material", ""));
        if (material == null) {
            material = Material.IRON_BLOCK;
        }
        ItemStack item = new ItemBuilder(material)
                .withStringDisplay(config.getString("elevator.item.display", "<aqua>Elevator Block</aqua>"), null)
                .withStringLore(config.getStringList("elevator.item.lore"), null)
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
        MainConfig.getInstance().getConfig().getStringList("elevator.item.recipe").forEach(itemName ->
                stackList.add(new ItemStack(ItemUtils.getMaterial(itemName, Material.AIR)))
        );
        ShapedRecipe recipe = new ShapedRecipe(getItemKey(), getElevatorBlock(), stackList);
        if (recipe.register()) {
            Loggers.info(Skylight.getInstance().getLogger(), "Registered Elevator Recipe");
        } else {
            Loggers.warning(Skylight.getInstance().getLogger(), "Elevator Recipe failed to register.");
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
