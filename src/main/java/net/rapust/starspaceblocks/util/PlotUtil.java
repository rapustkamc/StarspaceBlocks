package net.rapust.starspaceblocks.util;

import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.world.PlotAreaManager;
import lombok.experimental.UtilityClass;
import net.rapust.starspaceblocks.StarspaceBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PlotUtil {

    public List<Player> getPlayerOnPlot(Plot plot) {
        List<Player> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            Location location = player.getLocation();
            if (getPlotFromLocation(location) == plot) {
                players.add(player);
            }
        });

        return players;
    }

    public Plot getPlotFromLocation(Location location) {
        PlotAreaManager manager = StarspaceBlocks.getInstance().getPlotSquared().getPlotAreaManager();
        com.plotsquared.core.location.Location loc = com.plotsquared.core.location.Location.at(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
        PlotArea area = manager.getPlotArea(loc);
        if (area == null) return null;
        return area.getPlot(loc);
    }

}
