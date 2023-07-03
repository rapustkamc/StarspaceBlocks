package net.rapust.starspaceblocks.data.element;

import lombok.Getter;
import lombok.Setter;
import net.rapust.starspaceblocks.data.input.IField;
import net.rapust.starspaceblocks.data.input.type.MobField;
import net.rapust.starspaceblocks.util.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.List;

@Getter @Setter
public class VisualElement extends Element {

    private Location location;

    private Entity entity = null;
    private String value = "null";

    public VisualElement(ElementType type, List<IField> fields, Location location) {
        super(type, fields);
        this.location = location;
        if (fields.size() == 0) {
            return;
        }
        value = ChatColor.translateAlternateColorCodes('&', fields.get(fields.size() - 1).toString());
        if (location != null) {
            activate();
        }
    }

    @Override
    public VisualElement clone(List<IField> fields) {
        try {
            return new VisualElement(this.getType(), fields, location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void activate() {
        if (entity != null) {
            deactivate();
        }

        Location location = this.location.clone();
        location.setYaw(0);
        location.setPitch(0);
        location.add(0.5, 0, 0.5);

        switch (getType()) {
            case ONE_LINE_HOLOGRAM -> {
                ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setCustomNameVisible(true);
                armorStand.setCustomName(value);

                this.entity = armorStand;
            }

            case SHOW_MOB -> {
                if (getFields().size() == 0) {
                    return;
                }

                MobField field = null;
                for (IField f : getFields()) {
                    if (f instanceof MobField m) {
                        field = m;
                        break;
                    }
                }

                if (field == null) {
                    return;
                }

                EntityType type = field.getMob();
                location.add(0, 1, 0);

                LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, type);
                entity.setAI(false);
                entity.setGravity(false);
                entity.setCustomNameVisible(false);

                if (entity instanceof Ageable ageable) {
                    ageable.setAdult();
                }

                this.entity = entity;
            }
        }
    }

    public void deactivate() {
        if (entity == null) {
            return;
        }
        entity.remove();
        entity = null;
    }

    public void executeCommand(Player player) {
        if (getType() != ElementType.SHOW_MOB) {
            return;
        }
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), PlaceholderUtil.format(player, value.replace("%player%", player.getName())));
    }

}
