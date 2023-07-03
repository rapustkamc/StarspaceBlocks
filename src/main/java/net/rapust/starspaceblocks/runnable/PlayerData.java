package net.rapust.starspaceblocks.runnable;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerData {

    @Data
    public static class Loc {

        private final World world;
        private final int x;
        private final int y;
        private final int z;

        public boolean isEquals(Loc loc) {
            return loc.world == world && loc.x == x && loc.y == y && loc.z == z;
        }

        public Loc(Location location) {
            this.world = location.getWorld();
            this.x = location.getBlockX();
            this.y = location.getBlockY();
            this.z = location.getBlockZ();
        }

    }

    private final Loc location;
    private final List<PotionEffectType> effects = new ArrayList<>();

    public PlayerData(Player player) {
        this.location = new Loc(player.getLocation());
        player.getActivePotionEffects().forEach(potionEffect -> {
            effects.add(potionEffect.getType());
        });
    }

    public boolean hasLocationChanged(PlayerData playerData) {
        return !location.isEquals(playerData.getLocation());
    }

    public boolean hasGainedEffect(PlayerData playerData) {
        for (PotionEffectType effect : playerData.getEffects()) {
            if (!effects.contains(effect)) {
                return true;
            }
        }
        return false;
    }

}
