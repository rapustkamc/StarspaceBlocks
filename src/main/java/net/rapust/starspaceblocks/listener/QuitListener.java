package net.rapust.starspaceblocks.listener;

import net.rapust.starspaceblocks.util.AskUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!AskUtil.isAsking(player)) {
            return;
        }
        AskUtil.askingFor(player).destroy();
        AskUtil.reset(player);
    }

}
