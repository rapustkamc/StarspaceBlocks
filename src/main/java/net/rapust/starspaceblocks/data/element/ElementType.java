package net.rapust.starspaceblocks.data.element;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.rapust.starspaceblocks.data.input.type.*;
import net.rapust.starspaceblocks.util.InputUtil;
import net.rapust.starspaceblocks.data.input.IField;
import org.bukkit.Location;

import java.util.List;

@Getter
@AllArgsConstructor
public enum ElementType {

    STEP("STEP-$", new Class<?>[]{RadiusField.class}),
    JUMP("JUMP-$", new Class<?>[]{RadiusField.class}),
    UNDER("UNDER-$", new Class<?>[]{RadiusField.class}),
    OVER("OVER-$", new Class<?>[]{RadiusField.class}),
    SENDS_MESSAGE("SENDS-MESSAGE-$", new Class<?>[]{RadiusField.class}),
    GAINS_EFFECT("GAINS-EFFECT-$", new Class<?>[]{RadiusField.class}),
    L_CLICKS_BLOCK("L-CLICKS-BLOCK", new Class<?>[]{}),
    R_CLICKS_BLOCK("R-CLICKS-BLOCK", new Class<?>[]{}),
    DROPS("DROPS-$", new Class<?>[]{RadiusField.class}),
    PLAYER_ATTACKS("PLAYER-ATTACKS-$", new Class<?>[]{RadiusField.class}),
    PLAYER_IS_ATTACKED("PLAYER-IS-ATTACKED-$", new Class<?>[]{RadiusField.class}),
    PLAYER_COMMAND("PLAYER-COMMAND-$", new Class<?>[]{CommandField.class}),
    ONE_LINE_HOLOGRAM("ONE-LINE-HOLOGRAM-$", new Class<?>[]{HologramField.class}, true),
    SHOW_MOB("SHOW-MOB-$-$", new Class<?>[]{MobField.class, CommandField.class}, true);

    private final String name;
    private final Class<?>[] fields;
    private final boolean visual;

    ElementType(String name, Class<?>[] fields) {
        this.name = name;
        this.fields = fields;
        this.visual = false;
    }

    public static ElementType getByName(String name) {
        String[] values = name.split("-");
        for (ElementType elementType : values()) {
            String[] type = elementType.getName().split("-");
            boolean same = true;
            int rememberedValue = 0;

            for (String that : values) {
                String now = type[rememberedValue];
                rememberedValue++;
                if (now.equals("$")) {
                    rememberedValue--;
                    continue;
                }
                if (!that.equalsIgnoreCase(now)) {
                    same = false;
                    break;
                }
            }
            if (same) {
                return elementType;
            }
        }
        return null;
    }

    public static Element parseString(String string, Location location) {
        ElementType type = getByName(string);
        List<IField> fields = InputUtil.getFieldsElement(type, string, true);
        if (type.isVisual()) {
            return new VisualElement(type, fields, location);
        }
        return new Element(type, fields);
    }

    public int getIndex(IField field) {
        for (int i = 0; i < fields.length; i++) {
            Class<?> clazz = fields[i];
            if (clazz.isInstance(field)) {
                return i;
            }
        }
        return -1;
    }

}
