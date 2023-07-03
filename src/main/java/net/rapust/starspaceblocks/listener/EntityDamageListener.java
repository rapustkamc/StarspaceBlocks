package net.rapust.starspaceblocks.listener;

import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.block.PlacedBlock;
import net.rapust.starspaceblocks.data.element.VisualElement;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        for (PlacedBlock block : StarspaceBlocks.getInstance().getBlocksManager().getPlacedBlocks()) {
            if (block.getElement() instanceof VisualElement visualElement) {
                Entity that = visualElement.getEntity();
                if (that != null) {
                    if (that.getEntityId() == entity.getEntityId()) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

}
