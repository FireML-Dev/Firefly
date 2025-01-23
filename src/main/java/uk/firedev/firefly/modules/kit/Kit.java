package uk.firedev.firefly.modules.kit;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.api.utils.ItemUtils;
import uk.firedev.daisylib.command.CooldownHelper;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.daisylib.reward.Reward;
import uk.firedev.firefly.Firefly;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Kit {

    private static final Random random = new Random();

    private CooldownHelper cooldowns = CooldownHelper.cooldownHelper();
    private String name;
    private Material material;
    private Component display;
    private List<Component> lore;
    private boolean singleRandomReward;
    private boolean permissionOpen;
    private List<Reward> rewards;
    private String permission;
    private long guiCooldown;
    private boolean playerVisible;

    public Kit(@Nullable Section section) throws InvalidConfigurationException {
        if (section == null) {
            throw new InvalidConfigurationException("Kit config is not valid!");
        }
        this.name = section.getNameAsString();
        this.permission = section.getString("permission", "");
        this.material = ItemUtils.getMaterial(section.getString("material", ""), Material.SHULKER_BOX);
        this.display = ComponentMessage.fromString(section.getString("display", "<gold><bold>Kit")).getMessage();
        this.guiCooldown = section.getLong("gui-cooldown");
        this.playerVisible = section.getBoolean("player-visible", true);
        List<String> loreStrings = section.getStringList("lore");
        if (loreStrings.isEmpty()) {
            this.lore = List.of(
                    ComponentMessage.fromString("<green>Right Click to Claim</green>").getMessage()
            );
        } else {
            this.lore = loreStrings.stream().map(s -> ComponentMessage.fromString(s).getMessage()).toList();
        }
        this.singleRandomReward = section.getBoolean("single-random-reward", false);
        this.permissionOpen = section.getBoolean("permission-open");

        List<String> rewardsList = section.getStringList("contents");
        this.rewards = rewardsList.stream()
                .map(identifier -> new Reward(identifier, Firefly.getInstance()))
                .toList();
    }

    public Kit(@NotNull String name) throws InvalidConfigurationException {
        this(KitConfig.getInstance().getConfig().getSection("kits." + name));
    }

    public @NotNull Material getMaterial() {
        return this.material;
    }

    public @NotNull Component getDisplay() {
        return this.display;
    }

    public @NotNull List<Component> getLore() {
        return this.lore;
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
        ItemStack item = ItemStack.of(getMaterial());
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        meta.displayName(getDisplay());
        meta.lore(getLore());
        pdc.set(KitModule.getInstance().getKitKey(), PersistentDataType.STRING, getName());
        item.setItemMeta(meta);
        return item;
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

    public void awardKit(@NotNull Player player, boolean skipCooldown) {
        if (!skipCooldown) {
            if (isOnCooldown(player.getUniqueId())) {
                KitConfig.getInstance().getGUIOnCooldownMessage().sendMessage(player);
                return;
            }
            applyCooldown(player.getUniqueId());
        }
        giveToPlayer(player, null);
    }

    public void giveToPlayer(@NotNull Player player, @Nullable CommandSender sender) {
        ItemUtils.giveItem(buildItem(), player);
        ComponentReplacer replacer = ComponentReplacer.componentReplacer("kit", getName());
        KitConfig.getInstance().getAwardedReceiverMessage().applyReplacer(replacer).sendMessage(player);
        if (sender != null) {
            replacer.addReplacements("player", player.getName());
            KitConfig.getInstance().getAwardedCommandMessage().applyReplacer(replacer).sendMessage(sender);
        }
    }

}
