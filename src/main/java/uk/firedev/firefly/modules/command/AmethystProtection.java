package uk.firedev.firefly.modules.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.api.utils.ObjectUtils;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AmethystProtection implements Listener, Module {

    private static AmethystProtection instance = null;
    private static final List<UUID> warned = new ArrayList<>();

    private final CommandAPICommand command;
    private boolean loaded = false;

    private AmethystProtection() {
        command = new CommandAPICommand("amethystprotect")
                .withPermission("firefly.command.amethystprotect")
                .withShortDescription("Protects Amethyst")
                .withFullDescription("Protects Amethyst")
                .executesPlayer((player, arguments) -> {
                    PersistentDataContainer pdc = player.getPersistentDataContainer();
                    if (isDisabled(player)) {
                        pdc.set(getAmethystProtectKey(), PersistentDataType.BOOLEAN, false);
                        CommandConfig.getInstance().getAmethystProtectEnabledMessage().sendMessage(player);
                    } else {
                        pdc.set(getAmethystProtectKey(), PersistentDataType.BOOLEAN, true);
                        CommandConfig.getInstance().getAmethystProtectDisabledMessage().sendMessage(player);
                    }
                });
        Bukkit.getPluginManager().registerEvents(this, Firefly.getInstance());
    }

    public static AmethystProtection getInstance() {
        if (instance == null) {
            instance = new AmethystProtection();
        }
        return instance;
    }

    @Override
    public String getIdentifier() {
        return "AmethystProtection";
    }

    @Override
    public void load() {
        if (isLoaded()) {
            return;
        }
        Loggers.info(Firefly.getInstance().getComponentLogger(), "Registering AmethystProtect Command");
        command.register(Firefly.getInstance());
        loaded = true;
    }

    @Override
    public void reload() {
        if (!isLoaded()) {
            return;
        }
        // There is nothing to reload here :)
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            return;
        }
        // Unregister the event listener
        HandlerList.unregisterAll(this);
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    private NamespacedKey getAmethystProtectKey() {
        return ObjectUtils.createNamespacedKey("no-amethyst-protect", Firefly.getInstance());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.BUDDING_AMETHYST) {
            return;
        }
        Player player = event.getPlayer();
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (!pdc.getOrDefault(getAmethystProtectKey(), PersistentDataType.BOOLEAN, false)) {
            event.setCancelled(true);
            if (!warned.contains(player.getUniqueId())) {
                warned.add(player.getUniqueId());
                CommandConfig.getInstance().getAmethystProtectProtectedMessage().sendMessage(player);
            }
        }
    }

    public boolean isDisabled(Player player) {
        if (!isLoaded()) {
            return true;
        }
        return player.getPersistentDataContainer().getOrDefault(getAmethystProtectKey(), PersistentDataType.BOOLEAN, false);
    }

}
