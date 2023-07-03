package net.rapust.starspaceblocks.data.input.type;

import lombok.Getter;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.VarType;
import net.rapust.starspaceblocks.data.input.IField;
import net.rapust.starspaceblocks.util.MessageUtil;

public class HealthField implements IField {

    @Getter
    private int health;

    private AccessType required;
    private String comment;

    public HealthField(String input, Boolean ignore) throws Exception {
        try {
            this.health = Integer.parseInt(input);
        } catch (Exception exception) {
            if (!ignore) {
                throw new Exception(MessageUtil.get("input.errors.strToInt").replace("%str%", input));
            } else {
                health = 20;
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
        return toString().equals(string);
    }

    @Override
    public String toString() {
        return String.valueOf(health);
    }

    @Override
    public IField clone(String value) throws Exception {
        HealthField health = new HealthField(value, false);
        health.setRequired(required);
        health.setComment(comment);
        return health;
    }

}
