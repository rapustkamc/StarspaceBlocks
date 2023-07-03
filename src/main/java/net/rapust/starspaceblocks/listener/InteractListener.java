package net.rapust.starspaceblocks.listener;

import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.block.PlacedBlock;
import net.rapust.starspaceblocks.data.element.ElementType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        PlacedBlock placedBlock = StarspaceBlocks.getInstance().getBlocksManager().getBlock(block);
        if (placedBlock == null) {
            return;
        }

        if (action == Action.LEFT_CLICK_BLOCK) {
            if (placedBlock.getElement().getType() == ElementType.L_CLICKS_BLOCK) {
                placedBlock.checkAndPerform(player);
            }
            return;
        }

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (placedBlock.getElement().getType() == ElementType.R_CLICKS_BLOCK) {
                placedBlock.checkAndPerform(player);
            }
        }
    }

}
