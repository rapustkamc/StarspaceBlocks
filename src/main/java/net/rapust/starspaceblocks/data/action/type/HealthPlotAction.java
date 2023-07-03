package net.rapust.starspaceblocks.data.action.type;

import com.plotsquared.core.plot.Plot;
import lombok.Data;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.action.ActionType;
import net.rapust.starspaceblocks.data.action.Value;
import net.rapust.starspaceblocks.util.PlotUtil;
import org.bukkit.entity.Player;

@Data
public class HealthPlotAction implements IAction {

    private String name;
    private Value value;

    private Plot plot;

    private int health;

    private final Boolean setup;

    public HealthPlotAction(String name, Value value, Boolean setup, Plot plot) {
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
        return ActionType.SET_HEALTH_ON_PLOT;
    }

    @Override
    public void setValue(Value value) {
        this.value = value;
        try {
            this.health = Integer.parseInt(value.getUnformattedString());
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
        PlotUtil.getPlayerOnPlot(plot).forEach(p -> p.setHealth(health));
    }

}
