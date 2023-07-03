package net.rapust.starspaceblocks.listener;

import com.plotsquared.core.plot.Plot;
import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.data.element.ElementType;
import net.rapust.starspaceblocks.util.AskUtil;
import net.rapust.starspaceblocks.util.PlotUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!AskUtil.isAsking(player)) {
            Plot plot = PlotUtil.getPlotFromLocation(player.getLocation());
            StarspaceBlocks.getInstance().getBlocksManager().getBlocksOnPlotWithElement(plot, ElementType.SENDS_MESSAGE).forEach(block -> {
                block.checkAndPerform(player, block.distance(player.getLocation()));
            });
            return;
        }
        event.setCancelled(true);
        AskUtil.handleMessage(player, event.getMessage());
    }

}
