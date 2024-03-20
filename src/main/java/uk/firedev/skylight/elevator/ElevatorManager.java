package uk.firedev.skylight.elevator;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import uk.firedev.daisylib.builders.ItemBuilder;
import uk.firedev.daisylib.crafting.ShapedRecipe;
import uk.firedev.daisylib.utils.ComponentUtils;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.config.MainConfig;
import uk.firedev.skylight.config.MessageConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        new ElevatorCommand().registerCommand("elevator", this.plugin);
        registerRecipe();
        loaded = true;
    }

    public void reload() {
        if (!loaded) {
            return;
        }
    }

    public Component getBossBarTitle(Elevator elevator) {
        return ComponentUtils.parseComponent(
                MainConfig.getInstance().getConfig().getString(
                    "elevator.bossbar.title",
                    "<yellow>Floor {current} of {all}</yellow>"
                ),
                "current", String.valueOf(elevator.getCurrentPosition() + 1),
                "all", String.valueOf(elevator.getStack().size())
        );
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
            System.out.println("Elevator is null");
            return;
        }
        Location location = elevator.getTPLocation();
        location.setYaw(player.getYaw());
        location.setPitch(player.getPitch());
        if (!location.getBlock().isPassable()) {
            MessageConfig.getInstance().sendMessageFromConfig(player, "messages.elevator.unsafe-location");
            return;
        }
        System.out.println("preparing teleport");
        player.teleportAsync(location).thenAccept(aBoolean -> {
            if (!aBoolean) {
                MessageConfig.getInstance().sendMessageFromConfig(player, "messages.elevator.teleport-fail");
            } else {
                MessageConfig.getInstance().sendMessage(player, "You teleported. Congrats");
                elevator.handleBossBar(player);
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
                .withStringDisplay(config.getString("elevator.item.display", "<aqua>Elevator Block</aqua>"))
                .withStringLore(config.getStringList("elevator.item.lore"))
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .build();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(getItemKey(), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    //TODO no worky. come back later
    private void registerRecipe() {
        List<ItemStack> stackList = new ArrayList<>();
        MainConfig.getInstance().getConfig().getStringList("elevator.item.recipe").forEach(itemName -> {
            Material material;
            try {
                material = Material.valueOf(itemName.toUpperCase());
            } catch (IllegalArgumentException ex) {
                material = Material.AIR;
            }
            stackList.add(new ItemStack(material));
        });
        ShapedRecipe recipe = new ShapedRecipe(getItemKey(), getElevatorBlock(), stackList);
        System.out.println(stackList);
        if (!recipe.register()) {
            System.out.println("Recipe did not register");
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
