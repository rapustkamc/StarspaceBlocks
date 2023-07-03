package net.rapust.starspaceblocks.data.action.type;

import lombok.Data;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.action.ActionType;
import net.rapust.starspaceblocks.data.action.Value;
import net.rapust.starspaceblocks.util.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Data
public class CommandAction implements IAction {

    private String name;
    private Value value;

    private String command;

    private final Boolean setup;

    public CommandAction(String name, Value value, Boolean setup) {
        this.name = name;
        this.setup = setup;
        setValue(value);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ActionType getType() {
        return ActionType.COMMAND;
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
    public void perform(Player player) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), PlaceholderUtil.format(player, command.replace("%player%", player.getName())));
    }
}
