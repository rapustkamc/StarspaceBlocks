package net.rapust.starspaceblocks.data.action;

import org.bukkit.entity.Player;

public interface IAction {

    String getName();
    ActionType getType();

    void setValue(Value value) throws Exception;
    Value getValue();

    void perform(Player player) throws Exception;

}
