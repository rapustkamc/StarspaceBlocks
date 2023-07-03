package net.rapust.starspaceblocks.util;

import lombok.experimental.UtilityClass;
import net.rapust.starspaceblocks.StarspaceBlocks;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

@UtilityClass
public class MessageUtil {

    public String get(String code) {
        FileConfiguration config = StarspaceBlocks.getInstance().getConfig();

        if (!config.contains(code)) {
            return "Missing string: " + code;
        }

        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(code)));
    }

}
