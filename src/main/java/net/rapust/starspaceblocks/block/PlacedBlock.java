package net.rapust.starspaceblocks.block;

import com.plotsquared.core.plot.Plot;
import lombok.Data;
import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.element.Element;
import net.rapust.starspaceblocks.data.element.VisualElement;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.FieldType;
import net.rapust.starspaceblocks.data.input.IField;
import net.rapust.starspaceblocks.util.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.*;

@Data
public class PlacedBlock {

    private final int id;

    private OfflinePlayer owner;
    private final ConfigBlock configBlock;

    private final Element element;
    private List<IAction> actions;

    private World world = null;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    private boolean configured;

    private Plot plot = null;

    public PlacedBlock(int id, OfflinePlayer owner, ConfigBlock configBloc, Element element) {
        this.id = id;
        this.owner = owner;
        this.configBlock = configBloc;
        this.element = element;
        this.actions = new ArrayList<>(configBlock.getActions());
        this.configured = true;
    }

    public void askForActions() {
        Player player = owner.getPlayer();
        if (player == null) {
            return;
        }

        if (AskUtil.isAsking(player)) {
            return;
        }

        this.actions = new ArrayList<>(configBlock.getActions());
        this.configured = false;

        AskUtil.setAsking(player, this);
    }

    public void addAction(IAction action) {
        if (!actions.contains(action)) {
            actions.add(action);
        }
    }

    public void setLocation(Location location) {
        if (location == null) {
            this.world = null;
            this.x = 0;
            this.y = 0;
            this.z = 0;
            this.plot = null;
            try {
                updatePlot();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return;
        }

        if (element instanceof VisualElement visualElement) {
            visualElement.setLocation(location);
        }

        this.world = location.getWorld();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.plot = PlotUtil.getPlotFromLocation(location);
        try {
            updatePlot();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean isPlaced() {
        return world != null;
    }

    public double distance(Location location) {
        if (!isPlaced()) {
            return 0;
        }

        if (location.getWorld() != world) {
            return Double.MAX_VALUE;
        }

        return location.distance(getLocation());
    }

    public void destroy() {
        this.actions = new ArrayList<>(configBlock.getActions());
        getLocation().getBlock().setType(Material.AIR);
        this.world = null;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.plot = null;

        if (element instanceof VisualElement visualElement) {
            visualElement.deactivate();
        }

        if (owner.isOnline() && AskUtil.askingFor(owner.getPlayer()) == this) {
            AskUtil.reset(owner.getPlayer());
        }

        try {
            updatePlot();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (owner.isOnline()) {
            if (InventoryUtil.canHold(owner.getPlayer())) {
                give();
            }
        } else {
            if (!InventoryUtil.canHold(owner.getPlayer())) {
                StarspaceBlocks.getInstance().getBlocksManager().removeBlock(this);
            }
        }
    }

    private void updatePlot() throws Exception {
        for (IAction action : actions) {
            if (action.getType().isPlot()) {
                Method method = action.getClass().getMethod("setPlot", Plot.class);
                method.invoke(action, plot);
            }
        }
    }

    public void give() {
        if (!owner.isOnline()) {
            StarspaceBlocks.getInstance().getBlocksManager().removeBlock(this);
            return;
        }
        owner.getPlayer().getInventory().addItem(generateItem());
    }

    public Location getLocation() {
        if (world == null) return null;
        return new Location(world, x, y, z);
    }

    public void checkAndPerform(Player onPlayer, Object... strings) {
        if (!isConfigured()) {
            return;
        }

        Plot playersPlot = PlotUtil.getPlotFromLocation(onPlayer.getLocation());
        if (plot != playersPlot) {
            return;
        }

        List<IField> fields = element.getFields();
        boolean run = true;

        for (int i = 0; i < strings.length; i++) {
            String  string = String.valueOf(strings[i]);
            IField field = fields.get(i);
            try {
                boolean compare = field.compare(string);
                if (!compare) {
                    run = false;
                    break;
                }
            } catch (Exception e) {
                run = false;
                break;
            }
        }

        if (run) {
            new BukkitRunnable() {
                int i = 0;

                @Override
                public void run() {
                    if (i == actions.size()) {
                        cancel();
                    } else {
                        try {
                            actions.get(i).perform(onPlayer);
                        } catch (Exception e) {
                            Player player = owner.getPlayer();

                            if (player != null) {
                                String message = MessageUtil.get("messages.prefix") + MessageUtil.get("messages.trouble")
                                        .replace("%nameId%", configBlock.getKey())
                                        .replace("%location%", world.getName() + " " + x + " " + y + " " + z);

                                String error = e.getMessage();

                                if (error != null && !error.isEmpty()) {
                                    message = message.replace("%error%", error);
                                } else {
                                    message = message.replace("%error%", "UNKNOWN");
                                }

                                player.sendMessage(message);
                            }
                        }
                        i++;
                    }
                }

            }.runTaskTimer(StarspaceBlocks.getInstance(), 0L, configBlock.getActionsDelay());
        }
    }

    private ItemStack generateItem() {
        ItemStack item = new ItemStack(configBlock.getMaterial());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(configBlock.getName());
        meta.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString("uniqueid")), PersistentDataType.INTEGER, id);
        meta.setLore(getLore());

        item.setItemMeta(meta);
        return item;
    }

    private List<String> getLore() {
        String elementString = MessageUtil.get("strings.elementString");
        String actionString = MessageUtil.get("strings.actionString");
        String fieldStringOptional = MessageUtil.get("strings.actionFieldStringOptional");
        String fieldStringRequired = MessageUtil.get("strings.actionFieldStringRequired");

        List<String> configLore = StarspaceBlocks.getInstance().getConfig().getStringList("items.block.lore");
        List<String> result = new ArrayList<>();

        configLore.forEach(string -> {
            switch (string) {
                case "{element}":
                    result.add(elementString.replace("%element%", element.toString()));
                    break;
                case "{actions}":
                    for (int i = 0; i < actions.size(); i++) {
                        IAction action = actions.get(i);
                        result.add(actionString
                                .replace("%name%", action.getName())
                                .replace("%type%", action.getType().getName())
                                .replace("%value%", action.getValue().getLoreString()));
                        action.getValue().getObjects().forEach(object -> {
                            if (object instanceof IField field) {
                                if (field.getRequired() == AccessType.OPTIONAL) {
                                    result.add(fieldStringOptional
                                            .replace("%field%", Objects.requireNonNull(FieldType.getByField(field)).toString().toLowerCase())
                                            .replace("%comment%", field.getComment())
                                            .replace("%default%", field.toString()));
                                } else {
                                    result.add(fieldStringRequired
                                            .replace("%field%", Objects.requireNonNull(FieldType.getByField(field)).toString().toLowerCase())
                                            .replace("%comment%", field.getComment()));
                                }
                            }
                        });
                        if (i != actions.size() - 1) {
                            result.add(" ");
                        }
                    }
                    break;
                default:
                    result.add(ChatColor.translateAlternateColorCodes('&', string));
            }
        });

        return result;
    }

    public static PlacedBlock createBlock(Player player, ConfigBlock configBlock, String data) throws IllegalAccessException, IllegalArgumentException {
        HashMap<String, String> values = InputUtil.splitToValues(data);
        Set<String> keys = values.keySet();
        List<IField> newFields = new ArrayList<>();

        for (IField field : configBlock.getElement().getFields()) {
            String[] prefixes = FieldType.getPrefixes(field);
            boolean run = true;
            for (String prefix : prefixes) {
                if (!run) {
                    break;
                }
                for (String key : keys) {
                    if (key.equalsIgnoreCase(prefix)) {
                        if (field.getRequired() == AccessType.STRONG) {
                            throw new IllegalAccessException(Objects.requireNonNull(FieldType.getByField(field)).toString());
                        }
                        String value = values.get(key);
                        try {
                            newFields.add(field.clone(value));
                        } catch (Exception e) {
                            throw new IllegalArgumentException(e.getMessage());
                        }
                        run = false;
                    }
                }
            }
            if (run) {
                if (field.getRequired() == AccessType.REQUIRED) {
                    throw new IllegalStateException(FieldType.getByField(field).toString());
                }
                newFields.add(field);
            }
        }

        if (configBlock.getElement().getType().isVisual()) {
            VisualElement element = (VisualElement) configBlock.getElement();
            return new PlacedBlock(StarspaceBlocks.getInstance().getBlocksManager().getRandomId(), player, configBlock, element.clone(newFields));
        } else {
            return new PlacedBlock(StarspaceBlocks.getInstance().getBlocksManager().getRandomId(), player, configBlock, configBlock.getElement().clone(newFields));
        }
    }

}
