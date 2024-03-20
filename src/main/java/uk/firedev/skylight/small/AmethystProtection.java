package uk.firedev.skylight.small;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.command.ICommand;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.config.MessageConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AmethystProtection implements ICommand, Listener {

    private static AmethystProtection instance = null;
    private static final List<UUID> warned = new ArrayList<>();

    private boolean loaded = false;

    public static AmethystProtection getInstance() {
        if (instance == null) {
            instance = new AmethystProtection();
        }
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (isDisabled(player)) {
            pdc.set(getAmethystProtectKey(), PersistentDataType.BOOLEAN, false);
            MessageConfig.getInstance().sendMessageFromConfig(player, "messages.amethyst-protection.enabled");
        } else {
            pdc.set(getAmethystProtectKey(), PersistentDataType.BOOLEAN, true);
            MessageConfig.getInstance().sendMessageFromConfig(player, "messages.amethyst-protection.disabled");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    private NamespacedKey getAmethystProtectKey() {
        return ObjectUtils.createNamespacedKey("no-amethyst-protect", Skylight.getInstance());
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
                MessageConfig.getInstance().sendMessageFromConfig(player, "messages.amethyst-protection.protected");
            }
        }
    }

    public boolean isDisabled(Player player) {
        return player.getPersistentDataContainer().getOrDefault(getAmethystProtectKey(), PersistentDataType.BOOLEAN, false);
    }

    public void setLoaded() {
        loaded = true;
    }

    public boolean isLoaded() {
        return loaded;
    }

}
