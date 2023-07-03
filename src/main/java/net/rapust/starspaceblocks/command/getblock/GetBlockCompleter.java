package net.rapust.starspaceblocks.command.getblock;

import net.rapust.starspaceblocks.StarspaceBlocks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class GetBlockCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> first = List.of("help", "get", "info", "list");

        switch (args.length) {
            case 1:
                return first;
            case 2:
                String arg = args[0].toLowerCase();
                if (first.contains(arg) && !arg.equals("help")) {
                    List<String> blocks = new ArrayList<>();
                    StarspaceBlocks.getInstance().getBlocksManager().getConfigBlocks().forEach(block -> blocks.add(block.getKey()));
                    return blocks;
                } else {
                    return new ArrayList<>();
                }
            default:
                return new ArrayList<>();
        }
    }

}
