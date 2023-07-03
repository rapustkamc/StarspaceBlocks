package net.rapust.starspaceblocks.block;

import com.plotsquared.core.plot.Plot;
import lombok.Getter;
import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.block.ConfigBlock;
import net.rapust.starspaceblocks.block.PlacedBlock;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.action.ActionType;
import net.rapust.starspaceblocks.data.action.Value;
import net.rapust.starspaceblocks.data.element.Element;
import net.rapust.starspaceblocks.data.element.ElementType;
import net.rapust.starspaceblocks.data.element.VisualElement;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BlocksManager {

    @Getter
    private final List<ConfigBlock> configBlocks;
    @Getter
    private final List<PlacedBlock> placedBlocks;

    public BlocksManager() {
        configBlocks = new ArrayList<>();
        placedBlocks = new ArrayList<>();
        loadConfigBlocks();
        loadPlacedBlocks();
    }

    private void loadConfigBlocks() {
        File blocksFile = new File(StarspaceBlocks.getInstance().getDataFolder(), "blocks.yml");
        FileConfiguration blocks = YamlConfiguration.loadConfiguration(blocksFile);

        blocks.getKeys(false).forEach(key -> {
            try {

                String name = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(blocks.getString(key + ".name")));
                Material material = Material.valueOf(Objects.requireNonNull(blocks.getString(key + ".material")).toUpperCase());
                Element element = ElementType.parseString(blocks.getString(key + ".element"), null);
                int delay = blocks.contains(key + ".delay") ? blocks.getInt(key + ".delay") : 0;

                List<IAction> definedActions = new ArrayList<>();

                ConfigurationSection actions = blocks.getConfigurationSection(key + ".actions");
                if (actions != null) {
                    actions.getKeys(false).forEach(id -> {
                        String actionName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(actions.getString(id + ".name")));
                        ActionType actionType = ActionType.getByName(Objects.requireNonNull(actions.getString(id + ".type")).toUpperCase());
                        String value = actions.getString(id + ".value");
                        try {
                            definedActions.add(ActionType.parseString(actionName, actionType, new Value(value), true, null));
                        } catch (Exception exception) {
                            StarspaceBlocks.getInstance().getLogger().severe("Error while loading action for CONFIG block " + key + "! (" + id + ")");
                            exception.printStackTrace();
                        }
                    });
                }

                ConfigBlock configBlock = new ConfigBlock(key, name, material, element, definedActions, delay);
                configBlocks.add(configBlock);

                StarspaceBlocks.getInstance().getLogger().info("Loaded block " + key + ".");

            } catch (Exception exception) {
                StarspaceBlocks.getInstance().getLogger().severe("Error while loading block " + key + "!");
                exception.printStackTrace();
            }
        });
    }

    private void loadPlacedBlocks() {
        File dataFile = new File(StarspaceBlocks.getInstance().getDataFolder(), "data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

        data.getKeys(false).forEach(idString -> {
            try {
                int id = Integer.parseInt(idString);

                OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(Objects.requireNonNull(data.getString(id + ".owner"))));
                ConfigBlock configBlock = getConfigBlock(data.getString(id + ".configBlock"));

                String locationString = data.getString(id + ".location");
                Location location = null;
                if (!locationString.equals("none")) {
                    String[] values = locationString.split(";");
                    location = new Location(
                            Bukkit.getWorld(values[0]),
                            Integer.parseInt(values[1]),
                            Integer.parseInt(values[2]),
                            Integer.parseInt(values[3])
                    );
                }

                Element element = ElementType.parseString(data.getString(id + ".element"), location);

                List<IAction> actions = new ArrayList<>();
                data.getStringList(id + ".actions").forEach(actionString -> {
                    String[] values = actionString.split("<~>");
                    try {
                        actions.add(ActionType.parseString(values[0], ActionType.valueOf(values[1]), new Value(values[2]), false, null));
                    } catch (Exception exception) {
                        StarspaceBlocks.getInstance().getLogger().severe("Error while loading action for PLACED block " + idString + "! (" + actionString + ")");
                        exception.printStackTrace();
                    }
                });

                PlacedBlock placedBlock = new PlacedBlock(id, owner, configBlock, element);
                placedBlock.getActions().clear();
                placedBlock.getActions().addAll(actions);
                placedBlock.setLocation(location);

                placedBlocks.add(placedBlock);
            } catch (Exception exception) {
                StarspaceBlocks.getInstance().getLogger().severe("Error while loading PLACED block " + idString + "!");
                exception.printStackTrace();
            }
        });
    }

    public ConfigBlock getConfigBlock(String nameId) {
        for (ConfigBlock configBlock : configBlocks) {
            if (configBlock.getKey().equals(nameId)) {
                return configBlock;
            }
        }
        return null;
    }

    public void addBlock(PlacedBlock block) {
        if (!placedBlocks.contains(block)) {
            placedBlocks.add(block);
            block.give();
        }
    }

    public void removeBlock(PlacedBlock block) {
        placedBlocks.remove(block);
    }

    public PlacedBlock getBlock(int id) {
        for (PlacedBlock placedBlock : placedBlocks) {
            if (placedBlock.getId() == id) {
                return placedBlock;
            }
        }
        return null;
    }

    public PlacedBlock getBlock(Location location) {
        return getBlock(location.getBlock());
    }

    public PlacedBlock getBlock(Block block) {
        for (PlacedBlock placedBlock : placedBlocks) {
            if (placedBlock.getWorld() == block.getWorld() &&
                    placedBlock.getX() == block.getX() &&
                    placedBlock.getY() == block.getY() &&
                    placedBlock.getZ() == block.getZ()) {
                return placedBlock;
            }
        }
        return null;
    }

    public List<PlacedBlock> getBlocksOnPlot(Plot plot) {
        List<PlacedBlock> blocks = new ArrayList<>();
        placedBlocks.forEach(block -> {
            if (block.getPlot() == plot) {
                blocks.add(block);
            }
        });
        return blocks;
    }

    public List<PlacedBlock> getBlocksWithElement(Element element) {
        return getBlocksWithElement(element.getType());
    }

    public List<PlacedBlock> getBlocksWithElement(ElementType type) {
        List<PlacedBlock> blocks = new ArrayList<>();
        placedBlocks.forEach(block -> {
            if (block.getElement().getType() == type) {
                blocks.add(block);
            }
        });
        return blocks;
    }

    public List<PlacedBlock> getBlocksOnPlotWithElement(Plot plot, Element element) {
        return getBlocksOnPlotWithElement(plot, element.getType());
    }

    public List<PlacedBlock> getBlocksOnPlotWithElement(Plot plot, ElementType type) {
        List<PlacedBlock> blocks = new ArrayList<>();
        for (PlacedBlock placedBlock : placedBlocks) {
            if (placedBlock.getPlot() == plot && placedBlock.getElement().getType() == type) {
                blocks.add(placedBlock);
            }
        }
        return blocks;
    }

    public void deactivateAll() {
        placedBlocks.forEach(block -> {
            if (block.getElement() instanceof VisualElement visualElement) {
                visualElement.deactivate();
            }
        });
    }

    public void savePlacedBlocks() {
        File dataFile = new File(StarspaceBlocks.getInstance().getDataFolder(), "data.yml");
        dataFile.delete();
        try {
            dataFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

        placedBlocks.forEach(placedBlock -> {
            try {
                String id = String.valueOf(placedBlock.getId());

                String owner = placedBlock.getOwner().getUniqueId().toString();
                String configBlock = placedBlock.getConfigBlock().getKey();

                String location = "none";
                if (placedBlock.isPlaced()) {
                    location = placedBlock.getWorld().getName() + ";" + placedBlock.getX() + ";" + placedBlock.getY() + ";" + placedBlock.getZ();
                }

                String element = placedBlock.getElement().toString();

                List<String> actions = new ArrayList<>();
                placedBlock.getActions().forEach(action -> {
                    actions.add(action.getName() + "<~>" + action.getType().toString() + "<~>" + action.getValue().getUnformattedString());
                });

                data.set(id + ".owner", owner);
                data.set(id + ".configBlock", configBlock);
                data.set(id + ".location", location);
                data.set(id + ".element", element);
                data.set(id + ".actions", actions);
            } catch (Exception exception) {
                StarspaceBlocks.getInstance().getLogger().severe("Error while saving PLACED block " + placedBlock.getId() + "!");
                exception.printStackTrace();
            }
        });

        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getRandomId() {
        if (placedBlocks.size() > 800000) {
            return 0;
        }
        int id = (int) (Math.random() * 890000 + 100000);
        if (getBlock(id) != null) {
            return getRandomId();
        }
        return id;
    }

}
