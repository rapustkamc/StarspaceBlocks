package net.rapust.starspaceblocks.listener;

import net.rapust.starspaceblocks.StarspaceBlocks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        StarspaceBlocks.getInstance().getBlocksManager().getPlacedBlocks().forEach(block -> {
            if (block.getOwner().getUniqueId().equals(player.getUniqueId())) {
                block.setOwner(player);
            }
        });
    }

}
