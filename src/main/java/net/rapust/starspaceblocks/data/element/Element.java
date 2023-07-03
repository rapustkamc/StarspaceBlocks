package net.rapust.starspaceblocks.data.element;

import lombok.Data;
import net.rapust.starspaceblocks.data.input.IField;

import java.util.List;

@Data
public class Element {

    private ElementType type;
    private List<IField> fields;

    public Element(ElementType type, List<IField> fields) {
        this.type = type;
        this.fields = fields;
    }

    public Element clone(List<IField> fields) {
        try {
            return new Element(type, fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        String value = type.getName();
        StringBuilder result = new StringBuilder();

        int fieldNow = 0;
        for (char c : value.toCharArray()) {
            if (c == '$') {
                result.append(fields.get(fieldNow).toString());
                fieldNow++;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

}
