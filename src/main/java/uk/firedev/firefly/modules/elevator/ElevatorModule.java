package uk.firedev.firefly.modules.elevator;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import uk.firedev.daisylib.recipe.AbstractConfigRecipe;
import uk.firedev.daisylib.recipe.RecipeUtil;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.config.ModuleConfig;
import uk.firedev.firefly.modules.elevator.command.ElevatorCommand;
import uk.firedev.firefly.modules.elevator.placeholders.ElevatorLevelPlaceholder;
import uk.firedev.firefly.modules.teleportation.TeleportModule;
import uk.firedev.firefly.placeholders.FireflyPlaceholders;
import uk.firedev.firefly.utils.ItemBuilder;

public class ElevatorModule implements Module {

    private static ElevatorModule instance;
    private final Firefly plugin;
    private AbstractConfigRecipe<?> recipe = null;

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
        return ModuleConfig.getInstance().isModuleEnabled("elevators");
    }

    @Override
    public void init() {
        PluginManager pm = this.plugin.getServer().getPluginManager();
        pm.registerEvents(new ElevatorListener(), this.plugin);
        registerRecipe();
        new ElevatorCommand().initCommand();
        registerPlaceholders();
    }

    @Override
    public void reload() {
        ElevatorConfig.getInstance().reload();
        registerRecipe();
    }

    @Override
    public void unload() {}

    private void registerPlaceholders() {
        FireflyPlaceholders.get().add(new ElevatorLevelPlaceholder(this));
    }

    public void teleportPlayer(@NonNull Player player, @Nullable Elevator elevator) {
        if (!isConfigEnabled() || elevator == null || !elevator.isElevator()) {
            return;
        }
        Location location = elevator.getTPLocation();
        location.setYaw(player.getYaw());
        location.setPitch(player.getPitch());
        if (!location.getBlock().isPassable()) {
            ElevatorConfig.getInstance().getUnsafeLocationMessage().send(player);
            return;
        }
        boolean teleportManager = TeleportModule.getInstance().isConfigEnabled();
        final Location lastLocation = teleportManager ? TeleportModule.getInstance().getLastLocation(player) : null;
        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                elevator.handleBossBar(player);
                if (teleportManager && lastLocation != null) {
                    TeleportModule.getInstance().setLastLocation(player, lastLocation);
                }
            } else {
                MessageConfig.getInstance().getErrorOccurredMessage().send(player);
            }
        });
    }

    public NamespacedKey getItemKey() {
        return new NamespacedKey(Firefly.getInstance(), "elevator-block");
    }

    public boolean isElevatorBlock(ItemStack itemStack) {
        if (!isConfigEnabled()) {
            return false;
        }
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(getItemKey(), PersistentDataType.BOOLEAN, false);
    }

    public ItemStack getElevatorBlock() {
        if (!isConfigEnabled()) {
            return ItemStack.empty();
        }

        ConfigurationSection config = ElevatorConfig.getInstance().getConfig().getConfigurationSection("item");

        return ItemBuilder.fromConfigWithBaseItem(ItemStack.of(Material.IRON_BLOCK), config, null, null)
            .editItem(item -> {
                item.editPersistentDataContainer(pdc -> pdc.set(getItemKey(), PersistentDataType.BOOLEAN, true));
                return item;
            })
            .getItem();
    }
    
    private void registerRecipe() {
        if (this.recipe != null) {
            this.recipe.unregister();
            this.recipe = null;
        }
        if (!isConfigEnabled()) {
            return;
        }
        ConfigurationSection section = ElevatorConfig.getInstance().getConfig().getConfigurationSection("item.recipe");
        if (section == null) {
            Firefly.getInstance().getLogging().info("Elevator recipe not configured.");
            return;
        }
        AbstractConfigRecipe<?> recipe = RecipeUtil.getRecipe(section, getItemKey(), getElevatorBlock());
        if (recipe == null) {
            Firefly.getInstance().getLogging().info("Elevator recipe invalid.");
            return;
        }
        recipe.register();
        this.recipe = recipe;
        Firefly.getInstance().getLogging().info("Registered Elevator Recipe");
    }

}
