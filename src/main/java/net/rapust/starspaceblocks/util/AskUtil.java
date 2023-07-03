package net.rapust.starspaceblocks.util;

import com.plotsquared.core.plot.Plot;
import lombok.Data;
import lombok.experimental.UtilityClass;
import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.block.PlacedBlock;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.action.ActionType;
import net.rapust.starspaceblocks.data.action.Value;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.FieldType;
import net.rapust.starspaceblocks.data.input.IField;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@UtilityClass
public class AskUtil {

    @Data
    private static class BlockCreatingPlayer {

        private final PlacedBlock placedBlock;
        private int actionNum = 0;
        private int fieldNum = 0;

        private IAction activeAction;
        private IField activeField;

        List<IField> newFields = new ArrayList<>();

        List<IAction> newActions = new ArrayList<>();

        public BlockCreatingPlayer(PlacedBlock placedBlock) {
            this.placedBlock = placedBlock;
        }

        public void save() throws Exception {
            ActionType type = ActionType.getByAction(activeAction);
            Class<?> clazz = type.getClazz();

            String name = activeAction.getName();
            Value value = activeAction.getValue().clone(newFields);

            IAction newAction;
            if (type.isPlot()) {
                Plot plot = placedBlock.getPlot();
                newAction = (IAction) clazz.getConstructor(name.getClass(), value.getClass(), Boolean.class, Plot.class).newInstance(name, value, false, plot);
            } else {
                newAction = (IAction) clazz.getConstructor(name.getClass(), value.getClass(), Boolean.class).newInstance(name, value, false);
            }

            newActions.add(newAction);

            fieldNum = 0;
            newFields = new ArrayList<>();
        }

        public void close() {
            placedBlock.setActions(newActions);
            finish();
        }

        public void send() {
            Player player = placedBlock.getOwner().getPlayer();
            String prefix = MessageUtil.get("input.prefix");

            String title = MessageUtil.get("input.title").replace("%prefix%", prefix);

            String action = MessageUtil.get("input.action")
                    .replace("%name%", activeAction.getName())
                    .replace("%type%", activeAction.getType().toString())
                    .replace("%value%", activeAction.getValue().getLoreString());

            String field = MessageUtil.get("input.field")
                    .replace("%type%", FieldType.getByField(activeField).toString())
                    .replace("%rawType%", activeField.getType().toString());

            String comment = null;
            if (activeField.getComment() != null && !activeField.getComment().isEmpty()) {
                comment = MessageUtil.get("input.comment")
                        .replace("%comment%", activeField.getComment());
            }

            String canIgnore = null;
            if (activeField.getRequired() == AccessType.OPTIONAL) {
                canIgnore = MessageUtil.get("input.canIgnore")
                        .replace("%default%", activeField.toString());
            }

            List<String> messages = new ArrayList<>();
            for (String string : StarspaceBlocks.getInstance().getConfig().getStringList("input.next")) {
                switch (string) {
                    case "{title}":
                        messages.add(title);
                        break;
                    case "{action}":
                        messages.add(action);
                        break;
                    case "{field}":
                        messages.add(field);
                        break;
                    case "{comment}":
                        if (comment != null) {
                            messages.add(comment);
                        }
                        break;
                    case "{canIgnore}":
                        if (canIgnore != null) {
                            messages.add(canIgnore);
                        }
                        break;
                    default:
                        messages.add(ChatColor.translateAlternateColorCodes('&', string));
                }
            }

            String[] toSend = new String[messages.size()];
            messages.toArray(toSend);
            player.sendMessage(toSend);
        }

        public void finish() {
            Player player = placedBlock.getOwner().getPlayer();
            placedBlock.setConfigured(true);
            if (!player.isOnline()) {
                return;
            }
            player.sendMessage(MessageUtil.get("input.prefix") + MessageUtil.get("input.finish"));
        }

    }

    private final HashMap<Player, BlockCreatingPlayer> players = new HashMap<>();

    public boolean isAsking(Player player) {
        return players.containsKey(player);
    }

    public PlacedBlock askingFor(Player player) {
        if (!isAsking(player)) return null;
        return players.get(player).getPlacedBlock();
    }

    public void setAsking(Player player, PlacedBlock placedBlock) {
        BlockCreatingPlayer blockCreatingPlayer = new BlockCreatingPlayer(placedBlock);
        players.put(player, blockCreatingPlayer);

        int fields = 0;
        for (IAction action : placedBlock.getActions()) {
            for (Object o : action.getValue().getObjects()) {
                if (o instanceof IField) {
                    fields++;
                }
            }
        }

        if (fields == 0) {
            players.get(player).finish();
            player.playSound(player.getLocation(), Sound.valueOf(StarspaceBlocks.getInstance().getConfig().getString("sounds.input")), 1.0F, 1.0F);
            reset(player);
            return;
        }

        try {
            next(player);
        } catch (Exception e) {
            player.sendMessage(MessageUtil.get("input.prefix") + MessageUtil.get("input.errorAction").replace("%error%", e.getMessage()));
            blockCreatingPlayer.send();
            e.printStackTrace();
        }
    }

    public void reset(Player player) {
        players.remove(player);
    }

    public void handleMessage(Player player, String message) {
        BlockCreatingPlayer blockCreatingPlayer = players.get(player);

        IField inputField = blockCreatingPlayer.getActiveField();
        if (inputField == null) {
            return;
        }

        IField activeField = blockCreatingPlayer.getActiveField();
        IField newField;
        if (message.equals("-")) {
            if (activeField.getRequired() == AccessType.REQUIRED) {
                player.sendMessage(MessageUtil.get("input.prefix") + MessageUtil.get("input.shouldSpecify"));
                blockCreatingPlayer.send();
                return;
            }

            try {
                newField = activeField.clone(activeField.toString());
            } catch (Exception e) {
                player.sendMessage(MessageUtil.get("input.prefix") + MessageUtil.get("input.errorUnknown").replace("%error%", e.getMessage()));
                reset(player);
                blockCreatingPlayer.getPlacedBlock().destroy();
                e.printStackTrace();
                return;
            }
        } else {
            try {
                newField = activeField.clone(message);
            } catch (Exception e) {
                player.sendMessage(MessageUtil.get("input.prefix") + MessageUtil.get("input.errorField").replace("%error%", e.getMessage()));
                blockCreatingPlayer.send();
                System.out.println(isAsking(player));
                return;
            }
        }

        blockCreatingPlayer.getNewFields().add(newField);

        try {
            next(player);
        } catch (Exception e) {
            player.sendMessage(MessageUtil.get("input.prefix") + MessageUtil.get("input.errorAction").replace("%error%", e.getMessage()));
            blockCreatingPlayer.send();
        }
    }

    public void next(Player player) throws Exception {
        BlockCreatingPlayer blockCreatingPlayer = players.get(player);
        int actionNum = blockCreatingPlayer.getActionNum();
        int fieldNum = blockCreatingPlayer.getFieldNum();

        player.playSound(player.getLocation(), Sound.valueOf(StarspaceBlocks.getInstance().getConfig().getString("sounds.input")), 1.0F, 1.0F);

        List<IAction> actions = blockCreatingPlayer.getPlacedBlock().getActions();

        if (actions.size() == actionNum) {
            reset(player);
            blockCreatingPlayer.close();
            return;
        }

        IAction action = actions.get(actionNum);
        blockCreatingPlayer.setActiveAction(action);

        List<IField> fields = action.getValue().getFields();

        if (fieldNum == fields.size()) {
            actionNum++;
            blockCreatingPlayer.setActionNum(actionNum);
            blockCreatingPlayer.save();
            next(player);
            return;
        }

        IField field = fields.get(fieldNum);
        blockCreatingPlayer.setActiveField(field);

        fieldNum++;
        blockCreatingPlayer.setFieldNum(fieldNum);

        blockCreatingPlayer.send();
    }

}
