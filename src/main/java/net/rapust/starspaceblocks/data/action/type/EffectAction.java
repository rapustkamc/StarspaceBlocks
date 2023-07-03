package net.rapust.starspaceblocks.data.action.type;

import lombok.Data;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.action.ActionType;
import net.rapust.starspaceblocks.data.action.Value;
import net.rapust.starspaceblocks.data.input.type.EffectField;
import net.rapust.starspaceblocks.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@Data
public class EffectAction implements IAction {

    private String name;
    private Value value;

    private PotionEffect effect;

    private final Boolean setup;

    public EffectAction(String name, Value value, Boolean setup) {
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
        return ActionType.EFFECT;
    }

    @Override
    public void setValue(Value value) {
        this.value = value;
        try {
            EffectField effectField = new EffectField(value.getUnformattedString(), setup);
            this.effect = effectField.getEffect();
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
        player.addPotionEffect(effect);
    }

}
