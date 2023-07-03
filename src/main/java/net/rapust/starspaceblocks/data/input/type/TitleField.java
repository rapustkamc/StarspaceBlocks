package net.rapust.starspaceblocks.data.input.type;

import lombok.Getter;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.VarType;
import net.rapust.starspaceblocks.data.input.IField;

public class TitleField implements IField {

    @Getter
    private final String title;

    private AccessType required;
    private String comment;

    public TitleField(String title, Boolean ignore) {
        this.title = title;
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
        return title.equals(string);
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public IField clone(String value) {
        TitleField title = new TitleField(value, false);
        title.setRequired(required);
        title.setComment(comment);
        return title;
    }

}
