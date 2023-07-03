package net.rapust.starspaceblocks.listener;

import com.plotsquared.core.plot.Plot;
import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.data.element.ElementType;
import net.rapust.starspaceblocks.util.PlotUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.persistence.PersistentDataType;

public class DropListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        if (item.getItemStack().getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("uniqueid"), PersistentDataType.INTEGER)) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        Plot plot = PlotUtil.getPlotFromLocation(player.getLocation());
        StarspaceBlocks.getInstance().getBlocksManager().getBlocksOnPlotWithElement(plot, ElementType.DROPS).forEach(block -> {
            block.checkAndPerform(player, block.distance(player.getLocation()));
        });
    }

}
