package uk.firedev.firefly.modules.kit;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.addons.reward.RewardAddon;
import uk.firedev.daisylib.addons.reward.RewardAddonRegistry;
import uk.firedev.daisylib.builders.ItemBuilder;
import uk.firedev.daisylib.util.CooldownHelper;
import uk.firedev.daisylib.util.Utils;
import uk.firedev.daisylib.libs.messagelib.replacer.Replacer;
import uk.firedev.daisylib.util.VaultManager;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Kit {

    private static final Random random = new Random();

    private final CooldownHelper cooldowns = CooldownHelper.cooldownHelper();
    private final @NonNull ConfigurationSection config;
    private final String name;
    private final boolean singleRandomReward;
    private final boolean permissionOpen;
    private final List<String> rewards;
    private final String permission;
    private final long guiCooldown;
    private final boolean playerVisible;

    public Kit(@Nullable ConfigurationSection section) throws InvalidConfigurationException {
        if (section == null) {
            throw new InvalidConfigurationException("Kit config is null!");
        }
        this.config = section;
        this.name = section.getName();
        this.permission = section.getString("permission", "");
        this.guiCooldown = section.getLong("gui-cooldown");
        this.playerVisible = section.getBoolean("player-visible", true);
        this.singleRandomReward = section.getBoolean("single-random-reward", false);
        this.permissionOpen = section.getBoolean("permission-open");

        this.rewards = section.getStringList("contents");
    }

    public Kit(@NonNull String name) throws InvalidConfigurationException {
        this(KitConfig.getInstance().getConfig().getConfigurationSection("kits." + name));
    }

    public boolean singleRandomReward() {
        return this.singleRandomReward;
    }

    public boolean permissionOpen() { return this.permissionOpen; }

    public @NonNull String getName() {
        return this.name;
    }

    public @NonNull List<String> getRewards() {
        return this.rewards;
    }

    public boolean isPlayerVisible() { return playerVisible; }

    public boolean hasPermission(@NonNull Player player) {
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
        return ItemBuilder.fromConfigWithBaseItem(ItemStack.of(Material.SHULKER_BOX), itemSection, null, null)
            .editItem(item -> {
                item.editPersistentDataContainer(pdc ->
                    pdc.set(KitModule.getInstance().getKitKey(), PersistentDataType.STRING, getName())
                );
                return item;
            })
            .getItem();
    }

    public void processRewards(@NonNull Player player) {
        List<String> rewardList = getRewards();
        if (singleRandomReward()) {
            int index = random.nextInt(rewardList.size());
            String random = rewardList.get(index);
            RewardAddonRegistry.get().processString(random, player);
        } else {
            rewardList.forEach(reward -> RewardAddonRegistry.get().processString(reward, player));
        }
    }

    public boolean isOnCooldown(@NonNull UUID uuid) {
        return cooldowns.has(uuid);
    }

    public void applyCooldown(@NonNull UUID uuid) {
        cooldowns.apply(uuid, Duration.ofSeconds(getGuiCooldown()));
    }

    public long getGuiCooldown() {
        return guiCooldown;
    }

    public void giveToPlayerWithCooldown(@NonNull Player player, @Nullable CommandSender sender) {
        if (isOnCooldown(player.getUniqueId())) {
            KitConfig.getInstance().getOnCooldownMessage().send(player);
            return;
        }
        applyCooldown(player.getUniqueId());
        giveToPlayer(player, sender);
    }

    public void giveToPlayer(@NonNull Player player, @Nullable CommandSender sender) {
        player.give(buildItem());
        Replacer replacer = Replacer.replacer().addReplacement("kit", getName());
        KitConfig.getInstance().getAwardedReceiverMessage().replace(replacer).send(player);
        if (sender != null && sender != player) {
            replacer = replacer.addReplacement("player", player.getName());
            KitConfig.getInstance().getAwardedCommandMessage().replace(replacer).send(sender);
        }
    }

}
