package net.rapust.starspaceblocks.data.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.rapust.starspaceblocks.data.input.type.*;

@Getter
@AllArgsConstructor
public enum FieldType {

    COMMAND(CommandField.class, new String[]{"command","c"}),
    EFFECT(EffectField.class, new String[]{"effect","e"}),
    HEALTH(HealthField.class, new String[]{"health","h"}),
    MESSAGE(MessageField.class, new String[]{"message","m"}),
    RADIUS(RadiusField.class, new String[]{"radius","r"}),
    TITLE(TitleField.class, new String[]{"title","t"}),
    HOLOGRAM(HologramField.class, new String[]{"hologram","hol"}),
    MOB(MobField.class, new String[]{"mob"}),
    AMOUNT(AmountField.class, new String[]{"amount","a"});

    private final Class<? extends IField> clazz;
    private final String[] prefixes;

    public static FieldType getByPrefix(String prefix) {
        for (FieldType field : values()) {
            for (String p : field.getPrefixes()) {
                if (prefix.equalsIgnoreCase(p)) {
                    return field;
                }
            }
        }
        return null;
    }

    public static String[] getPrefixes(IField field) {
        FieldType f = getByField(field);
        return f != null ? f.prefixes : new String[0];
    }

    public static FieldType getByField(IField field) {
        for (FieldType f : values()) {
            if (f.getClazz().isInstance(field)) {
                return f;
            }
        }
        return null;
    }

}
