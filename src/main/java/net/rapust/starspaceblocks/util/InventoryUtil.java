package net.rapust.starspaceblocks.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class InventoryUtil {

    public boolean canHold(Player player) {
        int fullSlots = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                fullSlots++;
            }
        }
        return fullSlots < 36;
    }

}
