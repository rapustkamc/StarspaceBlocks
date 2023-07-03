package net.rapust.starspaceblocks.data.action.type;

import com.plotsquared.core.plot.Plot;
import lombok.Data;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.action.ActionType;
import net.rapust.starspaceblocks.data.action.Value;
import net.rapust.starspaceblocks.util.PlaceholderUtil;
import net.rapust.starspaceblocks.util.PlotUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Data
public class TitlePlotAction implements IAction {

    private String name;
    private Value value;

    private Plot plot;

    private String title;
    private String subTitle;

    private final Boolean setup;

    public TitlePlotAction(String name, Value value, Boolean setup, Plot plot) {
        this.name = name;
        setValue(value);
        this.setup = setup;
        this.plot = plot;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ActionType getType() {
        return ActionType.SEND_TITLE_TO_PLOT;
    }

    @Override
    public void setValue(Value value) {
        this.value = value;

        try {
            String[] values = value.getUnformattedString().split("<~>");

            if (values.length == 1) {
                title = ChatColor.translateAlternateColorCodes('&', values[0]);
                subTitle = "";
            } else {
                title = ChatColor.translateAlternateColorCodes('&', values[0]);
                subTitle = ChatColor.translateAlternateColorCodes('&', values[1]);
            }
        } catch (Exception exception) {
            if (!setup) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public void perform(Player player) throws Exception {
        PlotUtil.getPlayerOnPlot(plot).forEach(p -> p.sendTitle(PlaceholderUtil.format(p, title), PlaceholderUtil.format(p, subTitle)));
    }

}
