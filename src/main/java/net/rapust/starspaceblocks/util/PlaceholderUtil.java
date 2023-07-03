package net.rapust.starspaceblocks.util;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import net.rapust.starspaceblocks.StarspaceBlocks;
import org.bukkit.entity.Player;

@UtilityClass
public class PlaceholderUtil {

    public String format(Player player, String string) {
        if (!StarspaceBlocks.getInstance().isHasPlaceholderAPI()) {
            return string;
        }

        return PlaceholderAPI.setPlaceholders(player, string);
    }

}
