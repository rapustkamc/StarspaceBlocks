package net.rapust.starspaceblocks.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class PlayerUtil {

    public void checkPlayer(Player player) throws Exception {
        if (player == null) {
            throw new Exception("No player!");
        }
    }

}
