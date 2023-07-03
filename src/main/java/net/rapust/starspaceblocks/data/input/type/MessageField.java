package net.rapust.starspaceblocks.data.input.type;

import lombok.Getter;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.VarType;
import net.rapust.starspaceblocks.data.input.IField;

public class MessageField implements IField {

    @Getter
    private final String message;

    private AccessType required;
    private String comment;

    public MessageField(String message, Boolean ignore) {
        this.message = message;
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
        return message.equals(string);
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public IField clone(String value) {
        MessageField message = new MessageField(value, false);
        message.setRequired(required);
        message.setComment(comment);
        return message;
    }

}
