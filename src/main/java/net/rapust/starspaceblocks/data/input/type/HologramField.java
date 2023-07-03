package net.rapust.starspaceblocks.data.input.type;

import lombok.Getter;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.VarType;
import net.rapust.starspaceblocks.data.input.IField;

public class HologramField implements IField {

    @Getter
    private final String hologram;

    private AccessType required;
    private String comment;

    public HologramField(String hologram, Boolean ignore) {
        this.hologram = hologram;
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
        return hologram.equals(string);
    }

    @Override
    public String toString() {
        return hologram;
    }

    @Override
    public IField clone(String value) {
        HologramField hologram = new HologramField(value, false);
        hologram.setRequired(required);
        hologram.setComment(comment);
        return hologram;
    }

}
