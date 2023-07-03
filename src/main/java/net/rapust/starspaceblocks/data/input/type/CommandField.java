package net.rapust.starspaceblocks.data.input.type;

import lombok.Getter;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.VarType;
import net.rapust.starspaceblocks.data.input.IField;

public class CommandField implements IField {

    @Getter
    private final String command;

    private AccessType required;
    private String comment;

    public CommandField(String command, Boolean ignore) {
         this.command = command;
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
        return command.equalsIgnoreCase(string);
    }

    @Override
    public String toString() {
        return command;
    }

    @Override
    public IField clone(String value) {
        CommandField command = new CommandField(value, false);
        command.setRequired(required);
        command.setComment(comment);
        return command;
    }

}
