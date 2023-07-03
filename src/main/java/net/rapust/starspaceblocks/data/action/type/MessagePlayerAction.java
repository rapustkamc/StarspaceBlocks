package net.rapust.starspaceblocks.data.action.type;

import lombok.Data;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.action.ActionType;
import net.rapust.starspaceblocks.data.action.Value;
import net.rapust.starspaceblocks.util.PlaceholderUtil;
import net.rapust.starspaceblocks.util.PlayerUtil;
import org.bukkit.entity.Player;

@Data
public class MessagePlayerAction implements IAction {

    private String name;
    private Value value;

    private String message;

    private final Boolean setup;

    public MessagePlayerAction(String name, Boolean setup, Value value) {
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
        return ActionType.SEND_MESSAGE_TO_PLAYER;
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
        PlayerUtil.checkPlayer(player);
        player.sendMessage(PlaceholderUtil.format(player, message));
    }

}
