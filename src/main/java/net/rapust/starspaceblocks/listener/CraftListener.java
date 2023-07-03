package net.rapust.starspaceblocks.listener;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CraftListener implements Listener {

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        for (ItemStack stack : event.getInventory().getMatrix()) {
            if (stack != null && stack.getType() != Material.AIR) {
                ItemMeta meta = stack.getItemMeta();
                if (meta != null) {
                    if (meta.getPersistentDataContainer().has(NamespacedKey.fromString("uniqueid"), PersistentDataType.INTEGER)) {
                        event.getInventory().setResult(null);
                    }
                }
            }
        }
    }

}
