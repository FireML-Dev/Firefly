package uk.firedev.firefly.modules.kit;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.type.Fire;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.daisylib.VaultManager;
import uk.firedev.daisylib.libs.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.daisylib.message.component.ComponentReplacer;
import uk.firedev.daisylib.reward.Reward;
import uk.firedev.daisylib.utils.DurationFormatter;
import uk.firedev.daisylib.utils.ItemUtils;
import uk.firedev.firefly.Firefly;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Kit {

    private static final Random random = new Random();

    private static Map<UUID, Map<String, MyScheduledTask>> cooldowns = new ConcurrentHashMap<>();
    private static MyScheduledTask cleanupTask;
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
        if (cleanupTask == null) {
            cleanupTask = Firefly.getScheduler().runTaskTimer(() -> cooldowns.entrySet().removeIf(entry -> entry.getValue().isEmpty()), 300L, 300L);
        }
        this.name = section.getNameAsString();
        this.permission = section.getString("permission", "");
        this.material = ItemUtils.getMaterial(section.getString("material", ""), Material.SHULKER_BOX);
        this.display = new ComponentMessage(section.getString("display", "<gold><bold>Kit")).getMessage();
        this.guiCooldown = section.getLong("gui-cooldown");
        this.playerVisible = section.getBoolean("player-visible", true);
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

    public boolean isPlayerVisible() { return playerVisible; }

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

    public boolean isOnCooldown(@NotNull UUID uuid) {
        Map<String, MyScheduledTask> kits = cooldowns.get(uuid);
        return kits != null && kits.containsKey(getName());
    }

    public void applyCooldown(@NotNull UUID uuid) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>())
                .putIfAbsent(getName(), Firefly.getScheduler().runTaskLater(() -> removeCooldown(uuid), getGuiCooldownTicks()));
    }

    public void removeCooldown(@NotNull UUID uuid) {
        Map<String, MyScheduledTask> map = cooldowns.get(uuid);
        if (map != null) {
            MyScheduledTask task = map.remove(getName());
            if (task != null) {
                task.cancel();
            }
        }
    }

    public static void removeAllCooldowns(@NotNull UUID uuid) {
        Map<String, MyScheduledTask> map = cooldowns.remove(uuid);
        if (map != null) {
            map.forEach((string, myScheduledTask) -> myScheduledTask.cancel());
        }
    }

    public long getGuiCooldown() {
        return guiCooldown;
    }

    public long getGuiCooldownTicks() {
        return guiCooldown * 20;
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
        ComponentReplacer replacer = new ComponentReplacer().addReplacements("kit", getName());
        KitConfig.getInstance().getAwardedReceiverMessage().applyReplacer(replacer).sendMessage(player);
        if (sender != null) {
            replacer.addReplacements("player", player.getName());
            KitConfig.getInstance().getAwardedCommandMessage().applyReplacer(replacer).sendMessage(sender);
        }
    }

    private void startCleanupTask() {

    }

}
