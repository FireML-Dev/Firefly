package uk.firedev.firefly.modules.kit;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.addons.reward.RewardAddon;
import uk.firedev.daisylib.builders.ItemBuilder;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.daisylib.command.CooldownHelper;
import uk.firedev.messagelib.replacer.Replacer;

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
    private final List<String> rewards;
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

        this.rewards = section.getStringList("contents");
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

    public @NotNull List<String> getRewards() {
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
        List<String> rewardList = getRewards();
        if (singleRandomReward()) {
            int index = random.nextInt(rewardList.size());
            String random = rewardList.get(index);
            RewardAddon.processString(random, player);
        } else {
            rewardList.forEach(reward -> RewardAddon.processString(reward, player));
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
            KitConfig.getInstance().getOnCooldownMessage().send(player);
            return;
        }
        applyCooldown(player.getUniqueId());
        giveToPlayer(player, sender);
    }

    public void giveToPlayer(@NotNull Player player, @Nullable CommandSender sender) {
        ItemUtils.giveItem(buildItem(), player);
        Replacer replacer = Replacer.replacer().addReplacement("kit", getName());
        KitConfig.getInstance().getAwardedReceiverMessage().replace(replacer).send(player);
        if (sender != null && sender != player) {
            replacer = replacer.addReplacement("player", player.getName());
            KitConfig.getInstance().getAwardedCommandMessage().replace(replacer).send(sender);
        }
    }

}
