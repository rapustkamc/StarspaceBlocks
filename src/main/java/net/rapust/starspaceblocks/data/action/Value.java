package net.rapust.starspaceblocks.data.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.rapust.starspaceblocks.data.input.FieldType;
import net.rapust.starspaceblocks.data.input.IField;
import net.rapust.starspaceblocks.util.InputUtil;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Value {

    private List<Object> objects;

    public Value(String string) {
        objects = InputUtil.getFieldsAction(string, true);
    }

    public void addString(String s) {
        objects.add(s);
    }

    public void addInputField(IField field) {
        objects.add(field);
    }

    public List<IField> getFields() {
        List<IField> fields = new ArrayList<>();
        objects.forEach(object -> {
            if (object instanceof IField field) {
                fields.add(field);
            }
        });
        return fields;
    }

    public String getUnformattedString() {
        StringBuilder builder = new StringBuilder();
        objects.forEach(object -> {
            if (object instanceof IField inputField) {
                builder.append(inputField);
            } else if (object instanceof String string) {
                builder.append(string);
            }
        });
        return builder.toString();
    }

    public String getLoreString() {
        StringBuilder builder = new StringBuilder();
        objects.forEach(object -> {
            if (object instanceof IField inputField) {
                builder.append("[").append(FieldType.getByField(inputField).toString().toLowerCase()).append("]");
            } else if (object instanceof String string) {
                builder.append(string);
            }
        });
        return builder.toString();
    }

    public Value clone(List<IField> fields) {
        List<Object> newObjects = new ArrayList<>();
        int i = 0;
        for (Object object : objects) {
            if (object instanceof IField) {
                newObjects.add(fields.get(i));
                i++;
            } else if (object instanceof String) {
                newObjects.add(object);
            }
        }
        return new Value(newObjects);
    }

}
