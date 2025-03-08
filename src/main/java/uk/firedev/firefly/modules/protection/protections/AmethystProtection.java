package uk.firedev.firefly.modules.protection.protections;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.libs.commandapi.CommandAPICommand;
import uk.firedev.daisylib.libs.commandapi.CommandTree;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.modules.protection.ProtectionConfig;
import uk.firedev.firefly.placeholders.Placeholders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AmethystProtection implements SubModule {

    private static AmethystProtection instance = null;
    private static final List<UUID> warned = new ArrayList<>();

    private boolean loaded = false;
    private final CommandTree command = new CommandTree("amethystprotect")
        .withPermission("firefly.command.amethystprotect")
        .withHelp("Protects Amethyst", "Protects Amethyst")
        .executesPlayer(info -> {
            Player player = info.sender();
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            if (isDisabled(player)) {
                pdc.set(getAmethystProtectKey(), PersistentDataType.BOOLEAN, false);
                ProtectionConfig.getInstance().getAmethystProtectEnabledMessage().sendMessage(player);
            } else {
                pdc.set(getAmethystProtectKey(), PersistentDataType.BOOLEAN, true);
                ProtectionConfig.getInstance().getAmethystProtectDisabledMessage().sendMessage(player);
            }
        });

    private AmethystProtection() {}

    public static AmethystProtection getInstance() {
        if (instance == null) {
            instance = new AmethystProtection();
        }
        return instance;
    }

    @Override
    public boolean isConfigEnabled() {
        return ProtectionConfig.getInstance().isAmethystProtectEnabled();
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
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider -> {
            provider.addAudiencePlaceholder("amethyst_protected", audience -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                if (AmethystProtection.getInstance().isLoaded()) {
                    return Component.text(!AmethystProtection.getInstance().isDisabled(player));
                } else {
                    return Component.text(false);
                }
            });
        });
    }

    private NamespacedKey getAmethystProtectKey() {
        return new NamespacedKey(Firefly.getInstance(), "no-amethyst-protect");
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
                ProtectionConfig.getInstance().getAmethystProtectProtectedMessage().sendMessage(player);
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
