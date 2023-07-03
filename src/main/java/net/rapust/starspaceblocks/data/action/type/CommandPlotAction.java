package net.rapust.starspaceblocks.data.action.type;

import com.plotsquared.core.plot.Plot;
import lombok.Data;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.action.ActionType;
import net.rapust.starspaceblocks.data.action.Value;
import net.rapust.starspaceblocks.util.PlaceholderUtil;
import net.rapust.starspaceblocks.util.PlotUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@Data
public class CommandPlotAction implements IAction {

    private String name;
    private Value value;

    private Plot plot;

    private String command;

    private final Boolean setup;

    public CommandPlotAction(String name, Value value, Boolean setup, Plot plot) {
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
        return ActionType.COMMAND_ON_PLOT;
    }

    @Override
    public void setValue(Value value) {
        this.value = value;
        this.command = value.getUnformattedString();
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public void perform(Player player) throws Exception {
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        PlotUtil.getPlayerOnPlot(plot).forEach(p -> Bukkit.getServer().dispatchCommand(sender, PlaceholderUtil.format(p, command.replace("%player%", p.getName()))));
    }
}
