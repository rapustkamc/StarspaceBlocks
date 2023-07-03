package net.rapust.starspaceblocks.data.action.type;

import com.plotsquared.core.plot.Plot;
import lombok.Data;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.action.ActionType;
import net.rapust.starspaceblocks.data.action.Value;
import net.rapust.starspaceblocks.util.PlaceholderUtil;
import net.rapust.starspaceblocks.util.PlotUtil;
import org.bukkit.entity.Player;

@Data
public class MessagePlotAction implements IAction {

    private String name;
    private Value value;

    private Plot plot;

    private String message;

    private Boolean setup;

    public MessagePlotAction(String name, Value value, Boolean setup, Plot plot) {
        this.name = name;
        this.setup = setup;
        setValue(value);
        this.plot = plot;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ActionType getType() {
        return ActionType.SEND_MESSAGE_TO_PLOT;
    }

    @Override
    public void setValue(Value value) {
        this.value = value;
        this.message = value.getUnformattedString();
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public void perform(Player player) throws Exception {
        PlotUtil.getPlayerOnPlot(plot).forEach(p -> p.sendMessage(PlaceholderUtil.format(p, message)));
    }
}
