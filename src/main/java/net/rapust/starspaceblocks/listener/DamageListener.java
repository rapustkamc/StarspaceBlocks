package net.rapust.starspaceblocks.listener;

import com.plotsquared.core.plot.Plot;
import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.data.element.ElementType;
import net.rapust.starspaceblocks.block.BlocksManager;
import net.rapust.starspaceblocks.util.PlotUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.PLAYER || event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        Player damager = (Player) event.getDamager();
        Player target = (Player) event.getEntity();

        Plot plot = PlotUtil.getPlotFromLocation(damager.getLocation());

        StarspaceBlocks.getInstance().getBlocksManager().getBlocksOnPlotWithElement(plot, ElementType.PLAYER_ATTACKS).forEach(block -> {
            block.checkAndPerform(damager, block.distance(damager.getLocation()));
        });

        StarspaceBlocks.getInstance().getBlocksManager().getBlocksOnPlotWithElement(plot, ElementType.PLAYER_IS_ATTACKED).forEach(block -> {
            block.checkAndPerform(target, block.distance(target.getLocation()));
        });

        BlocksManager manager = StarspaceBlocks.getInstance().getBlocksManager();

        if (event.getFinalDamage() >= target.getHealth()) {
            for (ItemStack item : target.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        if (meta.getPersistentDataContainer().has(NamespacedKey.fromString("uniqueid"), PersistentDataType.INTEGER)) {
                            target.getInventory().remove(item);
                            int id = meta.getPersistentDataContainer().get(NamespacedKey.fromString("uniqueid"), PersistentDataType.INTEGER);
                            manager.removeBlock(manager.getBlock(id));
                        }
                    }
                }
            }
        }
    }

}
