package net.rapust.starspaceblocks.data.input;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VarType {

    STRING(String.class),
    INTEGER(Integer.class),
    BOOLEAN(Boolean.class);

    private final Class<?> clazz;

}
