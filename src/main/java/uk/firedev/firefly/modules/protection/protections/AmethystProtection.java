package uk.firedev.firefly.modules.protection.protections;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.command.CommandUtils;
import uk.firedev.firefly.CommandHolder;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.SubModule;
import uk.firedev.firefly.modules.protection.ProtectionConfig;
import uk.firedev.firefly.placeholders.Placeholders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AmethystProtection implements SubModule, Listener, CommandHolder {

    private static AmethystProtection instance = null;
    private static final List<UUID> warned = new ArrayList<>();

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
    public void init() {}

    @Override
    public void reload() {}

    @Override
    public void unload() {}

    @Override
    public void registerPlaceholders() {
        Placeholders.manageProvider(provider ->
            provider.addAudiencePlaceholder("amethyst_protected", audience -> {
                if (!(audience instanceof Player player)) {
                    return Component.text("Player is not available.");
                }
                return Component.text(!isDisabled(player));
            }));
    }

    private NamespacedKey getAmethystProtectKey() {
        return new NamespacedKey(Firefly.getInstance(), "no-amethyst-protect");
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!isConfigEnabled()) {
            return;
        }
        if (event.getBlock().getType() != Material.BUDDING_AMETHYST) {
            return;
        }
        Player player = event.getPlayer();
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (!pdc.getOrDefault(getAmethystProtectKey(), PersistentDataType.BOOLEAN, false)) {
            event.setCancelled(true);
            if (!warned.contains(player.getUniqueId())) {
                warned.add(player.getUniqueId());
                ProtectionConfig.getInstance().getAmethystProtectProtectedMessage().send(player);
            }
        }
    }

    public boolean isDisabled(Player player) {
        return player.getPersistentDataContainer().getOrDefault(getAmethystProtectKey(), PersistentDataType.BOOLEAN, false);
    }

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> get() {
        return Commands.literal("amethystprotect")
            .requires(stack -> isConfigEnabled() && stack.getSender().hasPermission(permission()))
            .executes(context -> {
                Player player = CommandUtils.requirePlayer(context.getSource());
                if (player == null) {
                    return 1;
                }
                PersistentDataContainer pdc = player.getPersistentDataContainer();
                if (isDisabled(player)) {
                    pdc.set(getAmethystProtectKey(), PersistentDataType.BOOLEAN, false);
                    ProtectionConfig.getInstance().getAmethystProtectEnabledMessage().send(player);
                } else {
                    pdc.set(getAmethystProtectKey(), PersistentDataType.BOOLEAN, true);
                    ProtectionConfig.getInstance().getAmethystProtectDisabledMessage().send(player);
                }
                return 1;
            })
            .build();
    }

    /**
     * @return The list of aliases this command should have.
     */
    @NotNull
    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public @NotNull String permission() {
        return "firefly.command.amethystprotect";
    }

    /**
     * @return The permission for executing this command on another player.
     */
    @NotNull
    @Override
    public String targetPermission() {
        return "firefly.command.amethystprotect";
    }

    /**
     * @return This command's description.
     */
    @Nullable
    @Override
    public String description() {
        return null;
    }

}
