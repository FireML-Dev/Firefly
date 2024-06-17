package uk.firedev.skylight.modules.kit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class KitListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        Kit kit = KitManager.getInstance().getKit(item);
        if (kit == null) {
            return;
        }
        event.setCancelled(true);
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (kit.permissionOpen() && !kit.hasPermission(player)) {
            return;
        }
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItem(EquipmentSlot.HAND, item);
        kit.processRewards(player);
    }

}
