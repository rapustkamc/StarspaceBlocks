package net.rapust.starspaceblocks.data.input;

public interface IField {

    AccessType getRequired();
    void setRequired(AccessType accessType);

    String getComment();
    void setComment(String comment);

    VarType getType();

    boolean compare(String string);

    String toString();

    IField clone(String value) throws Exception;

}
