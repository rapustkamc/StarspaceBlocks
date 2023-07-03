package net.rapust.starspaceblocks.data.input.type;

import lombok.Getter;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.VarType;
import net.rapust.starspaceblocks.data.input.IField;
import net.rapust.starspaceblocks.util.MessageUtil;

public class RadiusField implements IField {

    @Getter
    private int radius;

    private AccessType required;
    private String comment;

    public RadiusField(String input, Boolean ignore) throws Exception {
        try {
            this.radius = Integer.parseInt(input);
        } catch (Exception exception) {
            if (!ignore) {
                throw new Exception(MessageUtil.get("input.errors.strToInt").replace("%str%", input));
            } else {
                radius = Integer.MAX_VALUE;
            }
        }
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
        return VarType.INTEGER;
    }

    @Override
    public boolean compare(String string) {
        if (radius <= 0) {
            return true;
        }
        double distance = Double.parseDouble(string);
        return distance <= radius;
    }

    @Override
    public String toString() {
        return String.valueOf(radius);
    }

    @Override
    public IField clone(String value) throws Exception {
        RadiusField radius = new RadiusField(value, false);
        radius.setRequired(required);
        radius.setComment(comment);
        return radius;
    }

}
