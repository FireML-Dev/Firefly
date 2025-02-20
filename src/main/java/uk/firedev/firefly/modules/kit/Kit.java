package uk.firedev.firefly.modules.kit;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.api.builders.ItemBuilder;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.api.utils.ItemUtils;
import uk.firedev.daisylib.command.CooldownHelper;
import uk.firedev.daisylib.reward.Reward;
import uk.firedev.firefly.Firefly;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Kit {

    private static final Random random = new Random();

    private final CooldownHelper cooldowns = CooldownHelper.create();
    private final @NotNull ConfigurationSection config;
    private final String name;
    private final boolean singleRandomReward;
    private final boolean permissionOpen;
    private final List<Reward> rewards;
    private final String permission;
    private final long guiCooldown;
    private final boolean playerVisible;

    public Kit(@Nullable ConfigurationSection section) throws InvalidConfigurationException {
        if (section == null) {
            throw new InvalidConfigurationException("Kit config is not valid!");
        }
        this.config = section;
        this.name = section.getName();
        this.permission = section.getString("permission", "");
        this.guiCooldown = section.getLong("gui-cooldown");
        this.playerVisible = section.getBoolean("player-visible", true);
        this.singleRandomReward = section.getBoolean("single-random-reward", false);
        this.permissionOpen = section.getBoolean("permission-open");

        List<String> rewardsList = section.getStringList("contents");
        this.rewards = rewardsList.stream()
                .map(identifier -> new Reward(identifier, Firefly.getInstance()))
                .toList();
    }

    public Kit(@NotNull String name) throws InvalidConfigurationException {
        this(KitConfig.getInstance().getConfig().getConfigurationSection("kits." + name));
    }

    public boolean singleRandomReward() {
        return this.singleRandomReward;
    }

    public boolean permissionOpen() { return this.permissionOpen; }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull List<Reward> getRewards() {
        return this.rewards;
    }

    public boolean isPlayerVisible() { return playerVisible; }

    public boolean hasPermission(@NotNull Player player) {
        if (this.permission.isEmpty()) {
            return true;
        }
        if (VaultManager.getInstance().getPermissions() == null) {
            return player.hasPermission(this.permission);
        } else {
            return VaultManager.getInstance().getPermissions().has(player, this.permission);
        }
    }

    public ItemStack buildItem() {
        ConfigurationSection itemSection = config.getConfigurationSection("item");
        return ItemBuilder.createWithConfig(itemSection, null, null)
            .editItem(item -> {
                item.editPersistentDataContainer(pdc ->
                    pdc.set(KitModule.getInstance().getKitKey(), PersistentDataType.STRING, getName())
                );
                return item;
            })
            .getItem();
    }

    public void processRewards(@NotNull Player player) {
        List<Reward> rewardList = getRewards();
        if (singleRandomReward()) {
            int index = random.nextInt(rewardList.size());
            rewardList.get(index).rewardPlayer(player);
        } else {
            rewardList.forEach(reward -> reward.rewardPlayer(player));
        }
    }

    public boolean isOnCooldown(@NotNull UUID uuid) {
        return cooldowns.hasCooldown(uuid);
    }

    public void applyCooldown(@NotNull UUID uuid) {
        cooldowns.applyCooldown(uuid, Duration.ofSeconds(getGuiCooldown()));
    }

    public long getGuiCooldown() {
        return guiCooldown;
    }

    public void giveToPlayerWithCooldown(@NotNull Player player, @Nullable CommandSender sender) {
        if (isOnCooldown(player.getUniqueId())) {
            KitConfig.getInstance().getOnCooldownMessage().sendMessage(player);
            return;
        }
        applyCooldown(player.getUniqueId());
        giveToPlayer(player, null);
    }

    public void giveToPlayer(@NotNull Player player, @Nullable CommandSender sender) {
        ItemUtils.giveItem(buildItem(), player);
        ComponentReplacer replacer = ComponentReplacer.create("kit", getName());
        KitConfig.getInstance().getAwardedReceiverMessage().applyReplacer(replacer).sendMessage(player);
        if (sender != null && sender != player) {
            replacer.addReplacements("player", player.getName());
            KitConfig.getInstance().getAwardedCommandMessage().applyReplacer(replacer).sendMessage(sender);
        }
    }

}
