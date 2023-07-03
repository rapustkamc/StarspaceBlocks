package net.rapust.starspaceblocks.data.action.type;

import lombok.Data;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.action.ActionType;
import net.rapust.starspaceblocks.data.action.Value;
import net.rapust.starspaceblocks.util.PlayerUtil;
import org.bukkit.entity.Player;

@Data
public class HealthAction implements IAction {

    private String name;
    private Value value;

    private int health;

    private final Boolean setup;

    public HealthAction(String name, Value value, Boolean setup) {
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
        return ActionType.SET_HEALTH;
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
        PlayerUtil.checkPlayer(player);
        player.setHealth(health);
    }
}
