package net.rapust.starspaceblocks.listener;

import com.plotsquared.core.plot.Plot;
import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.block.PlacedBlock;
import net.rapust.starspaceblocks.data.element.ElementType;
import net.rapust.starspaceblocks.data.element.VisualElement;
import net.rapust.starspaceblocks.util.PlotUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class InteractEntityListener implements Listener {

    private long lastActivation = 0L;

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (lastActivation + 50 < System.currentTimeMillis()) {
            lastActivation = System.currentTimeMillis();
        } else {
            return;
        }

        Plot plot = PlotUtil.getPlotFromLocation(player.getLocation());
        for (PlacedBlock block : StarspaceBlocks.getInstance().getBlocksManager().getBlocksOnPlotWithElement(plot, ElementType.SHOW_MOB)) {
            VisualElement visualElement = (VisualElement) block.getElement();
            if (visualElement.getEntity().getEntityId() == entity.getEntityId()) {
                visualElement.executeCommand(player);
                break;
            }
        }
    }

}
