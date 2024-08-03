package uk.firedev.firefly.modules.kit;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.reward.Reward;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.firefly.Firefly;

import java.util.List;
import java.util.Random;

public class Kit {

    private static final Random random = new Random();

    private String name;
    private Material material;
    private Component display;
    private List<Component> lore;
    private boolean singleRandomReward;
    private boolean permissionOpen;
    private List<Reward> rewards;
    private String permission;

    public Kit(@NotNull Section section) {
        construct(section);
    }

    public Kit(@NotNull String name) throws InvalidConfigurationException {
        Section section = KitConfig.getInstance().getConfig().getSection("kits." + name);
        if (section == null) {
            throw new InvalidConfigurationException();
        }
        construct(section);
    }

    private void construct(@NotNull Section section) {
        this.name = section.getNameAsString();
        this.permission = section.getString("permission", "");
        this.material = ItemUtils.getMaterial(section.getString("material", ""), Material.SHULKER_BOX);
        this.display = new ComponentMessage(section.getString("display", "<gold><bold>Kit")).getMessage();
        List<String> loreStrings = section.getStringList("lore");
        if (loreStrings.isEmpty()) {
            this.lore = List.of(
                new ComponentMessage("<green>Right Click to Claim</green>").getMessage()
            );
        } else {
            this.lore = loreStrings.stream().map(s -> new ComponentMessage(s).getMessage()).toList();
        }
        this.singleRandomReward = section.getBoolean("single-random-reward", false);
        this.permissionOpen = section.getBoolean("permission-open");

        List<String> rewardsList = section.getStringList("contents");
        this.rewards = rewardsList.stream()
                .map(identifier -> new Reward(identifier, Firefly.getInstance()))
                .toList();
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

    public String getConfigKey() {
        return "kits." + getName();
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull List<Reward> getRewards() {
        return this.rewards;
    }

    public boolean hasPermission(@NotNull Player player) {
        if (this.permission.isEmpty()) {
            return true;
        }
        if (VaultManager.getPermissions() == null) {
            return player.hasPermission(this.permission);
        } else {
            return VaultManager.getPermissions().has(player, this.permission);
        }
    }

    public ItemStack buildItem() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        meta.displayName(getDisplay());
        meta.lore(getLore());
        pdc.set(KitManager.getInstance().getKitKey(), PersistentDataType.STRING, getConfigKey());
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

}
