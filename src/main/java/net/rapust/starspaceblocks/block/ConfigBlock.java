package net.rapust.starspaceblocks.block;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.element.Element;
import net.rapust.starspaceblocks.data.element.ElementType;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.FieldType;
import net.rapust.starspaceblocks.data.input.IField;
import net.rapust.starspaceblocks.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@AllArgsConstructor
public class ConfigBlock {

    private final String key;
    private final String name;
    private final Material material;
    private final Element element;
    private final List<IAction> actions;
    private final long actionsDelay;

    public void displayInfo(Player player) {
        String title = MessageUtil.get("messages.prefix") + MessageUtil.get("messages.infoTitle").replace("%nameId%", key);

        List<String> messages = StarspaceBlocks.getInstance().getConfig().getStringList("messages.info");
        List<String> result = new ArrayList<>();

        result.add(title);

        HashMap<String, String> values = new HashMap<>();

        String elementString = MessageUtil.get("strings.elementString").replace("%element%", getElementString());

        values.put("nameId", key);
        values.put("name", name);
        values.put("elementString", elementString);

        messages.forEach(message -> formatMessage(message, values, result));

        String[] toSend = new String[result.size()];
        result.toArray(toSend);
        player.sendMessage(toSend);
    }

    public String getElementString() {
        ElementType elementType = this.element.getType();
        StringBuilder element = new StringBuilder();

        int fieldNow = 0;
        for (char c : elementType.getName().toCharArray()) {
            if (c == '$') {
                element.append("[").append(FieldType.getByField(this.element.getFields().get(fieldNow)).toString().toLowerCase()).append("]");
                fieldNow++;
            } else {
                element.append(c);
            }
        }
        return element.toString();
    }

    private void formatMessage(String message, HashMap<String, String> values, List<String> result) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        switch (message) {
            case "{elementInputFields}":
                addElementFields(result);
                return;

            case "{actions}":
                addActions(result);
                return;

            default:
                for (String key : values.keySet()) {
                    String string = values.get(key);
                    message = message.replace("%" + key + "%", string);
                }
        }

        result.add(message);
    }

    private void addElementFields(List<String> result) {
        String elementFieldStringOptional = MessageUtil.get("strings.elementFieldStringOptional");
        String elementFieldStringRequired = MessageUtil.get("strings.elementFieldStringRequired");
        String elementFieldStringStrong = MessageUtil.get("strings.elementFieldStringStrong");

        element.getFields().forEach(field -> {
            switch (field.getRequired()) {
                case OPTIONAL -> result.add(elementFieldStringOptional
                        .replace("%field%", FieldType.getByField(field).toString().toLowerCase())
                        .replace("%comment%", field.getComment())
                        .replace("%default%", field.toString()));

                case REQUIRED -> result.add(elementFieldStringRequired
                        .replace("%field%", FieldType.getByField(field).toString().toLowerCase())
                        .replace("%comment%", field.getComment()));

                case STRONG -> result.add(elementFieldStringStrong
                        .replace("%field%", FieldType.getByField(field).toString().toLowerCase())
                        .replace("%value%", field.toString()));
            }
        });
    }

    private void addActions(List<String> result) {
        String actionString = MessageUtil.get("strings.actionString");
        String actionFieldStringOptional = MessageUtil.get("strings.actionFieldStringOptional");
        String actionFieldStringRequired = MessageUtil.get("strings.actionFieldStringRequired");

        AtomicBoolean prevHas = new AtomicBoolean(false);
        for (int i = 0; i < actions.size(); i++) {
            IAction action = actions.get(i);
            result.add(actionString
                    .replace("%name%", action.getName())
                    .replace("%type%", action.getType().getName())
                    .replace("%value%", action.getValue().getLoreString()));

            action.getValue().getObjects().forEach(object -> {
                if (object instanceof IField field) {
                    prevHas.set(true);
                    if (field.getRequired() == AccessType.OPTIONAL) {
                        result.add(actionFieldStringOptional
                                .replace("%field%", Objects.requireNonNull(FieldType.getByField(field)).toString().toLowerCase())
                                .replace("%comment%", field.getComment())
                                .replace("%default%", field.toString()));
                    } else {
                        result.add(actionFieldStringRequired
                                .replace("%field%", Objects.requireNonNull(FieldType.getByField(field)).toString().toLowerCase())
                                .replace("%comment%", field.getComment()));
                    }
                }
            });

            if (i != actions.size() - 1 && prevHas.get()) {
                result.add(" ");
            }

            prevHas.set(false);
        }
    }

}
