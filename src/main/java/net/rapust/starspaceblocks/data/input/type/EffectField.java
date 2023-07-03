package net.rapust.starspaceblocks.data.input.type;

import lombok.Getter;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.VarType;
import net.rapust.starspaceblocks.data.input.IField;
import net.rapust.starspaceblocks.util.MessageUtil;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectField implements IField {

    @Getter
    private final PotionEffect effect;

    private AccessType required;
    private String comment;

    public EffectField(String input, Boolean ignore) throws Exception {
        String[] values = input.split(":");
        if (values.length != 3) {
            if (!ignore) {
                throw new Exception(MessageUtil.get("input.errors.effect.values"));
            } else {
                values = new String[]{"SPEED","3","3"};
            }
        }

        PotionEffectType type = PotionEffectType.getByName(values[0].toUpperCase());
        if (type == null) {
            throw new Exception(MessageUtil.get("input.errors.effect.type").replace("%type%", values[0].toUpperCase()));
        }

        int duration;
        try {
            duration  = Integer.parseInt(values[1]) * 20;
        } catch (Exception exception) {
            throw new Exception(MessageUtil.get("input.errors.strToInt").replace("%str%", values[1]));
        }

        int amplifier;
        try {
            amplifier  = Integer.parseInt(values[2]);
        } catch (Exception exception) {
            throw new Exception(MessageUtil.get("input.errors.strToInt").replace("%str%", values[2]));
        }

        effect = new PotionEffect(type, duration, amplifier);
    }

    @Override
    public AccessType getRequired() {
        return required;
    }

    @Override
    public void setRequired(AccessType required) {
        this.required = required;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public VarType getType() {
        return VarType.STRING;
    }

    @Override
    public boolean compare(String string) {
        return toString().equals(string);
    }

    @Override
    public String toString() {
        return effect.getType().getName() + ":" + effect.getDuration() / 20 + ":" + effect.getAmplifier();
    }

    @Override
    public IField clone(String value) throws Exception {
        EffectField effect = new EffectField(value, false);
        effect.setRequired(required);
        effect.setComment(comment);
        return effect;
    }

}
