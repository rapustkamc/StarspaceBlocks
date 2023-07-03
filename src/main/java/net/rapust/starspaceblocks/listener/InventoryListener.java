package net.rapust.starspaceblocks.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }
        if (event.getWhoClicked().getOpenInventory().getTitle().equals("Crafting")) {
            return;
        }
        ItemStack stack = inventory.getItem(event.getSlot());
        if (stack == null) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }
        if (meta.getPersistentDataContainer().has(NamespacedKey.fromString("uniqueid"), PersistentDataType.INTEGER)) {
            event.setCancelled(true);
        }
    }

}
