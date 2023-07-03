package net.rapust.starspaceblocks.listener;

import net.rapust.starspaceblocks.StarspaceBlocks;
import net.rapust.starspaceblocks.block.PlacedBlock;
import net.rapust.starspaceblocks.data.element.VisualElement;
import net.rapust.starspaceblocks.util.InventoryUtil;
import net.rapust.starspaceblocks.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PlaceBreakListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();

        if (!meta.getPersistentDataContainer().has(NamespacedKey.fromString("uniqueid"), PersistentDataType.INTEGER)) {
            return;
        }

        int id = meta.getPersistentDataContainer().get(NamespacedKey.fromString("uniqueid"), PersistentDataType.INTEGER);

        PlacedBlock placedBlock = StarspaceBlocks.getInstance().getBlocksManager().getBlock(id);

        if (!placedBlock.getOwner().getUniqueId().equals(player.getUniqueId())) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(StarspaceBlocks.getInstance(), () -> {
                player.setItemInHand(null);
            }, 1L);
        }

        placedBlock.setLocation(event.getBlock().getLocation().clone());
        placedBlock.askForActions();

        if (player.getGameMode() == GameMode.CREATIVE) {
            player.setItemInHand(null);
        }

        if (placedBlock.getElement() instanceof VisualElement visualElement) {
            visualElement.activate();
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        PlacedBlock placedBlock = StarspaceBlocks.getInstance().getBlocksManager().getBlock(event.getBlock());
        if (placedBlock == null) {
            return;
        }

        event.setCancelled(true);

        if (!InventoryUtil.canHold(player)) {
            player.sendMessage(MessageUtil.get("messages.prefix") + MessageUtil.get("messages.cantHold"));
            player.playSound(player.getLocation(), Sound.valueOf(StarspaceBlocks.getInstance().getConfig().getString("sounds.cantBreak")), 1.0F, 1.0F);
            return;
        }

        if (!placedBlock.getOwner().getUniqueId().equals(player.getUniqueId()) && !placedBlock.getPlot().getOwner().equals(player.getUniqueId())) {
            player.sendMessage(MessageUtil.get("messages.prefix") + MessageUtil.get("messages.cantBreak"));
            player.playSound(player.getLocation(), Sound.valueOf(StarspaceBlocks.getInstance().getConfig().getString("sounds.cantBreak")), 1.0F, 1.0F);
            return;
        }

        Bukkit.getScheduler().runTaskLater(StarspaceBlocks.getInstance(), placedBlock::destroy, 1L);
        player.playSound(player.getLocation(), Sound.valueOf(StarspaceBlocks.getInstance().getConfig().getString("sounds.broke")), 1.0F, 1.0F);
    }

}
