package net.rapust.starspaceblocks.command.getblock;

import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.block.ConfigBlock;
import net.rapust.starspaceblocks.block.PlacedBlock;
import net.rapust.starspaceblocks.block.BlocksManager;
import net.rapust.starspaceblocks.util.AskUtil;
import net.rapust.starspaceblocks.util.InventoryUtil;
import net.rapust.starspaceblocks.util.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetBlockCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only for players!");
            return true;
        }

        BlocksManager manager = StarspaceBlocks.getInstance().getBlocksManager();

        String prefix = MessageUtil.get("commands.getblock.prefix");
        String help = prefix + MessageUtil.get("commands.getblock.help");

        if (args.length < 2) {
            if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
                player.sendMessage(prefix + MessageUtil.get("commands.getblock.listTitle"));
                String listString = MessageUtil.get("commands.getblock.listString");
                manager.getConfigBlocks().forEach(configBlock -> {
                    player.sendMessage(listString
                            .replace("%nameId%", configBlock.getKey())
                            .replace("%name%", configBlock.getName())
                            .replace("%string%", configBlock.getElementString()));
                });
                return true;
            }
            player.sendMessage(help);
            return true;
        }

        String action = args[0];
        String nameId = args[1];

        ConfigBlock configBlock = manager.getConfigBlock(nameId);

        if (configBlock == null) {
            player.sendMessage(prefix + MessageUtil.get("commands.getblock.notFound").replace("%nameId%", nameId));
            return true;
        }

        if (action.equalsIgnoreCase("info")) {
            configBlock.displayInfo(player);
            return true;
        }

        if (!action.equalsIgnoreCase("get")) {
            player.sendMessage(help);
            return true;
        }

        if (AskUtil.isAsking(player)) {
            player.sendMessage(prefix + MessageUtil.get("commands.getblock.alreadyCreating"));
            return true;
        }

        if (!InventoryUtil.canHold(player)) {
            player.sendMessage(prefix + MessageUtil.get("commands.getblock.cantHold"));
            return true;
        }

        StringBuilder data = new StringBuilder();

        for (int i = 2; i < args.length; i++) {
            String s = args[i];
            data.append(s);
            if (i != args.length - 1) {
                data.append(" ");
            }
        }

        PlacedBlock placedBlock;

        try {
            placedBlock = PlacedBlock.createBlock(player, configBlock, data.toString());
            manager.addBlock(placedBlock);
            player.sendMessage(prefix + MessageUtil.get("commands.getblock.success"));
            player.playSound(player.getLocation(), Sound.valueOf(StarspaceBlocks.getInstance().getConfig().getString("sounds.get")), 1.0F, 1.0F);
        } catch (IllegalAccessException exception) {
            player.sendMessage(prefix + MessageUtil.get("commands.getblock.cantChange").replace("%field%", exception.getMessage()));
        } catch (IllegalStateException exception) {
            player.sendMessage(prefix + MessageUtil.get("commands.getblock.missingArguments").replace("%field%", exception.getMessage()));
        } catch (IllegalArgumentException exception) {
            player.sendMessage(prefix + MessageUtil.get("commands.getblock.error").replace("%error%", exception.getMessage()));
        }

        return true;
    }

}
