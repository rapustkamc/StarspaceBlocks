package net.rapust.starspaceblocks.listener;

import com.plotsquared.core.plot.Plot;
import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.data.element.ElementType;
import net.rapust.starspaceblocks.util.PlotUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Plot plot = PlotUtil.getPlotFromLocation(player.getLocation());

        StarspaceBlocks.getInstance().getBlocksManager().getBlocksOnPlotWithElement(plot, ElementType.PLAYER_COMMAND).forEach(block -> {
            block.checkAndPerform(player, event.getMessage().substring(1));
        });
    }

}
