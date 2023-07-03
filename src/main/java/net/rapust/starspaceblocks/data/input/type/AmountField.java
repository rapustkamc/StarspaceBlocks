package net.rapust.starspaceblocks.data.input.type;

import lombok.Getter;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.IField;
import net.rapust.starspaceblocks.data.input.VarType;
import net.rapust.starspaceblocks.util.MessageUtil;

public class AmountField implements IField {

    @Getter
    private int amount;

    private AccessType required;
    private String comment;

    public AmountField(String input, Boolean ignore) throws Exception {
        try {
            this.amount = Integer.parseInt(input);
        } catch (Exception exception) {
            if (!ignore) {
                throw new Exception(MessageUtil.get("input.errors.strToInt").replace("%str%", input));
            } else {
                amount = 0;
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
        return String.valueOf(amount);
    }

    @Override
    public IField clone(String value) throws Exception {
        AmountField amountField = new AmountField(value, false);
        amountField.setRequired(required);
        amountField.setComment(comment);
        return amountField;
    }

}
