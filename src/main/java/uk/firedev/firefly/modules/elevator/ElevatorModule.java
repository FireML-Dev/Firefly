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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.builders.ItemBuilder;
import uk.firedev.daisylib.recipe.AbstractRecipe;
import uk.firedev.daisylib.recipe.RecipeUtil;
import uk.firedev.daisylib.recipe.ShapedRecipe;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.daisylib.utils.PlayerHelper;
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
    private AbstractRecipe<?> recipe = null;

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
        if (isLoaded()) {
            return;
        }
        PluginManager pm = this.plugin.getServer().getPluginManager();
        pm.registerEvents(new ElevatorListener(), this.plugin);
        ElevatorConfig.getInstance().init();
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
        registerRecipe();
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
        Placeholders.manageProvider(provider ->
            provider.addAudiencePlaceholder("elevator_level", audience -> {
                if (!isLoaded()) {
                    return MessageConfig.getInstance().getFeatureDisabledMessage().toSingleMessage().get();
                }
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                Block block = PlayerHelper.getPlayerStandingOn(player);
                Elevator elevator = new Elevator(block);
                if (elevator.isElevator()) {
                    return Component.text("N/A");
                }
                return Component.text(elevator.getCurrentPosition());
            }));
    }

    public void teleportPlayer(@NotNull Player player, @Nullable Elevator elevator) {
        if (elevator == null || !elevator.isElevator()) {
            return;
        }
        Location location = elevator.getTPLocation();
        location.setYaw(player.getYaw());
        location.setPitch(player.getPitch());
        if (!location.getBlock().isPassable()) {
            ElevatorConfig.getInstance().getUnsafeLocationMessage().send(player);
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
                MessageConfig.getInstance().getErrorOccurredMessage().send(player);
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
        ConfigurationSection config = ElevatorConfig.getInstance().getConfig().getConfigurationSection("item");

        return ItemBuilder.createWithConfig(config, null, null)
            .editItem(item -> {
                item.editPersistentDataContainer(pdc -> pdc.set(getItemKey(), PersistentDataType.BOOLEAN, true));
                return item;
            })
            .getItem();
    }
    
    private void registerRecipe() {
        if (this.recipe != null) {
            this.recipe.unregister();
        }
        ConfigurationSection section = ElevatorConfig.getInstance().getConfig().getConfigurationSection("item.recipe");
        if (section == null) {
            Loggers.info(Firefly.getInstance().getComponentLogger(), "Elevator recipe not configured.");
            return;
        }
        AbstractRecipe<?> recipe = RecipeUtil.getRecipe(section, getItemKey(), getElevatorBlock());
        if (recipe == null) {
            Loggers.info(Firefly.getInstance().getComponentLogger(), "Elevator recipe invalid.");
            return;
        }
        recipe.register();
        this.recipe = recipe;
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registered Elevator Recipe");
    }

}
