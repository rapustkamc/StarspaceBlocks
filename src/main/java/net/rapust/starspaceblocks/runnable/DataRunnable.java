package net.rapust.starspaceblocks.runnable;

import com.plotsquared.core.plot.Plot;
import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.data.element.ElementType;
import net.rapust.starspaceblocks.block.BlocksManager;
import net.rapust.starspaceblocks.util.PlotUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DataRunnable {

    private static int id = -1;
    private final HashMap<Player, PlayerData> data = new HashMap<>();

    public DataRunnable() {
        if (id != -1) {
            Bukkit.getScheduler().cancelTask(id);
        }

        BlocksManager manager = StarspaceBlocks.getInstance().getBlocksManager();

        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(StarspaceBlocks.getInstance(), () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {

               PlayerData oldData = data.get(player);
               PlayerData newData = new PlayerData(player);
               if (oldData != null) {

                   if (oldData.hasLocationChanged(newData)) {
                       Plot plot = PlotUtil.getPlotFromLocation(player.getLocation());

                       int x = newData.getLocation().getX();
                       int y = newData.getLocation().getY();
                       int z = newData.getLocation().getZ();

                       manager.getBlocksOnPlotWithElement(plot, ElementType.STEP).forEach(block -> {
                           if (x == block.getX() && y == block.getY() + 1 && z == block.getZ()) {
                               block.checkAndPerform(player, block.distance(player.getLocation()));
                           }
                       });

                       manager.getBlocksOnPlotWithElement(plot, ElementType.UNDER).forEach(block -> {
                           if (y < block.getY()) {
                               block.checkAndPerform(player, block.distance(player.getLocation()));
                           }
                       });

                       manager.getBlocksOnPlotWithElement(plot, ElementType.OVER).forEach(block -> {
                           if (y > block.getY()) {
                               block.checkAndPerform(player, block.distance(player.getLocation()));
                           }
                       });
                   }

                   if (oldData.hasGainedEffect(newData)) {
                       Plot plot = PlotUtil.getPlotFromLocation(player.getLocation());
                       manager.getBlocksOnPlotWithElement(plot, ElementType.GAINS_EFFECT).forEach(block -> {
                           block.checkAndPerform(player, block.distance(player.getLocation()));
                       });
                   }

               }

               data.put(player, new PlayerData(player));

            });
        }, 0L, 12L);
    }

}
