package net.rapust.starspaceblocks.listener;

import com.plotsquared.core.plot.Plot;
import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.data.element.ElementType;
import net.rapust.starspaceblocks.util.PlotUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class JumpListener implements Listener {

    @EventHandler
    public void onJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getFrom().getX() != event.getTo().getX()) return;
        if (event.getFrom().getZ() != event.getTo().getZ()) return;
        double dif = event.getTo().getY() - event.getFrom().getY();

        if (dif < 0.35) {
            return;
        }

        Plot plot = PlotUtil.getPlotFromLocation(player.getLocation());
        StarspaceBlocks.getInstance().getBlocksManager().getBlocksOnPlotWithElement(plot, ElementType.JUMP).forEach(block -> {
            block.checkAndPerform(player, block.distance(player.getLocation()));
        });
    }

}
