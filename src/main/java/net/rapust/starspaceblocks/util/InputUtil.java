package net.rapust.starspaceblocks.util;

import lombok.experimental.UtilityClass;
import net.rapust.starspaceblocks.data.element.ElementType;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.FieldType;
import net.rapust.starspaceblocks.data.input.IField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@UtilityClass
public class InputUtil {

    private enum CatchingType {
        NO,
        TYPE,
        DEFAULT_VALUE,
        COMMENT
    }

    public List<IField> getFieldsElement(ElementType type, String string, Boolean ignore) {
        List<IField> fields = new ArrayList<>();

        StringBuilder noBuilder = new StringBuilder();
        StringBuilder typeBuilder = new StringBuilder();
        StringBuilder defaultValueBuilder = new StringBuilder();
        StringBuilder commentBuilder = new StringBuilder();

        CatchingType catchingType = CatchingType.NO;

        List<String> unusualStrings = getUnusualString(string);
        List<String> unusedStrings = new ArrayList<>();
        List<Integer> skip = new ArrayList<>();

        for (char c : string.toCharArray()) {
            switch (c) {
                case '[':
                    catchingType = CatchingType.TYPE;
                    if (!noBuilder.toString().isEmpty()) {
                        unusedStrings.add(noBuilder.toString());
                    }
                    noBuilder = new StringBuilder();
                    break;
                case ']':
                    catchingType = CatchingType.NO;
                    try {
                        IField field = createField(
                                typeBuilder.toString(),
                                defaultValueBuilder.toString(),
                                commentBuilder.toString(),
                                ignore
                        );
                        int index = type.getIndex(field);
                        skip.add(index);
                        fields.add(field);
                    } catch (Exception ignored) {
                    }
                    typeBuilder = new StringBuilder();
                    defaultValueBuilder = new StringBuilder();
                    commentBuilder = new StringBuilder();
                    break;
                case '|':
                    catchingType = CatchingType.DEFAULT_VALUE;
                    break;
                case ';':
                    catchingType = CatchingType.COMMENT;
                    break;
                default:
                    switch (catchingType) {
                        case TYPE -> typeBuilder.append(c);
                        case DEFAULT_VALUE -> defaultValueBuilder.append(c);
                        case COMMENT -> commentBuilder.append(c);
                        case NO -> noBuilder.append(c);
                    }
            }

        }

        if (!noBuilder.toString().isEmpty()) {
            unusedStrings.add(noBuilder.toString());
        }

        List<String> splitStrings = new ArrayList<>();
        unusedStrings.forEach(s -> splitStrings.addAll(List.of(s.split("-"))));

        Class<?>[] template = type.getFields();

        int connect = 0;
        for (String unusual : unusualStrings) {
            for (String split : splitStrings) {
                if (unusual.equals(split)) {
                    if (skip.contains(connect)) {
                        continue;
                    }

                    Class<?> clazz = template[connect];
                    try {
                        IField field = (IField) clazz.getConstructor(split.getClass(), Boolean.class).newInstance(split, ignore);
                        field.setRequired(AccessType.STRONG);
                        fields.add(field);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    connect++;
                }
            }
        }

        IField[] sortedFields = new IField[fields.size()];

        for (IField field : fields) {
            Class<? extends IField> fieldClass = FieldType.getByField(field).getClazz();

            for (int i = 0; i < template.length; i++) {
                Class<?> toCheck = template[i];

                if (fieldClass == toCheck) {
                    sortedFields[i] = field;
                }
            }
        }

        return List.of(sortedFields);
    }

    private List<String> getUnusualString(String string) {
        List<String> result = new ArrayList<>();
        String[] values = string.split("-");
        for (ElementType elementType : ElementType.values()) {
            String[] type = elementType.getName().split("-");
            boolean same = true;
            int rememberedValue = 0;

            for (String that : values) {
                String now = type[rememberedValue];
                rememberedValue++;
                if (now.equals("$")) {
                    rememberedValue--;
                    result.add(that);
                    continue;
                }
                if (!that.equalsIgnoreCase(now)) {
                    same = false;
                    break;
                }
            }
            if (same) {
                return result;
            }
        }
        return result;
    }

    public List<Object> getFieldsAction(String string, Boolean ignore) {
        List<Object> fields = new ArrayList<>();

        StringBuilder noBuilder = new StringBuilder();
        StringBuilder typeBuilder = new StringBuilder();
        StringBuilder defaultValueBuilder = new StringBuilder();
        StringBuilder commentBuilder = new StringBuilder();

        CatchingType catchingType = CatchingType.NO;

        for (char c : string.toCharArray()) {
            switch (c) {
                case '[':
                    catchingType = CatchingType.TYPE;
                    if (!noBuilder.toString().isEmpty()) {
                        fields.add(noBuilder.toString());
                    }
                    noBuilder = new StringBuilder();
                    break;
                case ']':
                    catchingType = CatchingType.NO;
                    try {
                        fields.add(createField(
                                typeBuilder.toString(),
                                defaultValueBuilder.toString(),
                                commentBuilder.toString(),
                                ignore
                        ));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    typeBuilder = new StringBuilder();
                    defaultValueBuilder = new StringBuilder();
                    commentBuilder = new StringBuilder();
                    break;
                case '|':
                    catchingType = CatchingType.DEFAULT_VALUE;
                    break;
                case ';':
                    catchingType = CatchingType.COMMENT;
                    break;
                default:
                    switch (catchingType) {
                        case TYPE -> typeBuilder.append(c);
                        case DEFAULT_VALUE -> defaultValueBuilder.append(c);
                        case COMMENT -> commentBuilder.append(c);
                        case NO -> noBuilder.append(c);
                    }
            }

        }

        if (!noBuilder.toString().isEmpty()) {
            fields.add(noBuilder.toString());
        }

        return fields;
    }

    private IField createField(String type, String defaultValue, String comment, Boolean ignore) throws Exception {
        FieldType fieldType = FieldType.getByPrefix(type);
        IField field = fieldType.getClazz().getConstructor(defaultValue.getClass(), Boolean.class).newInstance(defaultValue, ignore);
        field.setComment(comment);
        if (defaultValue.isEmpty()) {
            field.setRequired(AccessType.REQUIRED);
        } else {
            field.setRequired(AccessType.OPTIONAL);
        }
        return field;
    }

    private enum Status {
        PRE,
        POST
    }

    public HashMap<String, String> splitToValues(String string) {
        HashMap<String, String> values = new HashMap<>();

        boolean in = false;
        Status status = Status.PRE;

        StringBuilder pre = new StringBuilder();
        StringBuilder post = new StringBuilder();

        for (char c : string.toCharArray()) {
            switch (c) {
                case '(':
                    in = true;
                    status = Status.PRE;
                    break;
                case ')':
                    in  = false;
                    status = Status.PRE;
                    values.put(pre.toString(), post.toString());
                    pre = new StringBuilder();
                    post = new StringBuilder();
                    break;
                case '=':
                    status = Status.POST;
                    break;
                default:
                    if (in) {
                        switch (status) {
                            case PRE -> pre.append(c);
                            case POST -> post.append(c);
                        }
                    }
            }
        }

        return values;
    }

}
