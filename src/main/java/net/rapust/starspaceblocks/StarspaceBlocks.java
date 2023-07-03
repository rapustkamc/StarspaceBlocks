package net.rapust.starspaceblocks;

import com.plotsquared.core.PlotSquared;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.rapust.starspaceblocks.command.getblock.GetBlockCommand;
import net.rapust.starspaceblocks.command.getblock.GetBlockCompleter;
import net.rapust.starspaceblocks.listener.*;
import net.rapust.starspaceblocks.block.BlocksManager;
import net.rapust.starspaceblocks.runnable.DataRunnable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@Getter
public class StarspaceBlocks extends JavaPlugin {

    @Getter
    private static StarspaceBlocks instance;

    private BlocksManager blocksManager;

    private PlotSquared plotSquared = null;
    private Economy economy = null;

    private boolean hasEconomy = false;
    private boolean hasPlaceholderAPI = false;
    private boolean hasWorldEdit = false;

    @Override
    public void onEnable() {
        instance = this;

        PluginManager manager = Bukkit.getPluginManager();

        if (!loadPlugins(manager)) {
            return;
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getConfig().options().copyDefaults();
            saveDefaultConfig();
        }

        File blocksFile = new File(getDataFolder(), "blocks.yml");
        if (!blocksFile.exists()) {
            saveResource("blocks.yml", false);
        }

        File dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Can't create data file!");
                e.printStackTrace();
            }
        }

        blocksManager = new BlocksManager();

        new DataRunnable();

        getCommand("getblock").setExecutor(new GetBlockCommand());
        getCommand("getblock").setTabCompleter(new GetBlockCompleter());

        manager.registerEvents(new ChatListener(), this);
        manager.registerEvents(new PlaceBreakListener(), this);
        manager.registerEvents(new JumpListener(), this);
        manager.registerEvents(new InteractListener(), this);
        manager.registerEvents(new DropListener(), this);
        manager.registerEvents(new DamageListener(), this);
        manager.registerEvents(new CommandListener(), this);
        manager.registerEvents(new InventoryListener(), this);
        manager.registerEvents(new CraftListener(), this);
        manager.registerEvents(new QuitListener(), this);
        manager.registerEvents(new InteractEntityListener(), this);
        manager.registerEvents(new EntityDamageListener(), this);
        manager.registerEvents(new JoinListener(), this);

        getLogger().info("Plugin is enabled!");
    }

    @Override
    public void onDisable() {
        blocksManager.savePlacedBlocks();
        blocksManager.deactivateAll();
        getLogger().info("Plugin is disabled!");
    }

    private boolean loadPlugins(PluginManager manager) {
        if (manager.getPlugin("PlotSquared") != null) {
            plotSquared = PlotSquared.get();
            getLogger().info("Loaded 'PlotSquared'!");
        } else {
            getLogger().severe("Can't find 'PlotSquared'! Can't load without it!");
            setEnabled(false);
            return false;
        }

        if (manager.getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                getLogger().severe("Error while loading 'Vault'!");
            } else {
                hasEconomy = true;
                economy = rsp.getProvider();
                getLogger().info("Loaded 'Vault'!");
            }
        } else {
            getLogger().warning("Can't find 'Vault'! Loading without it.");
        }

        if (manager.getPlugin("PlaceholderAPI") != null) {
            // no logic
            hasPlaceholderAPI = true;
            getLogger().info("Loaded 'PlaceholderAPI'!");
        } else {
            getLogger().warning("Can't find 'PlaceholderAPI'! Loading without it.");
        }

        if (manager.getPlugin("WorldEdit") != null) {
            // no logic
            hasWorldEdit = true;
            getLogger().info("Loaded 'WorldEdit'!");
        } else {
            getLogger().warning("Can't find 'WorldEdit'! Loading without it.");
        }

        return true;
    }

}
