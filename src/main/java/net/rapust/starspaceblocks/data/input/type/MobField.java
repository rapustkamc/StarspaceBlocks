package net.rapust.starspaceblocks.data.input.type;

import lombok.Getter;
import net.rapust.starspaceblocks.data.input.AccessType;
import net.rapust.starspaceblocks.data.input.VarType;
import net.rapust.starspaceblocks.data.input.IField;
import net.rapust.starspaceblocks.util.MessageUtil;
import org.bukkit.entity.EntityType;

public class MobField implements IField {

    @Getter
    private EntityType mob;

    private AccessType required;
    private String comment;

    public MobField(String input, Boolean ignore) throws Exception {
        try {
            mob = EntityType.valueOf(input.toUpperCase());
        } catch (Exception exception) {
            if (!ignore) {
                throw new Exception(MessageUtil.get("input.errors.mob.type").replace("%type%", input.toUpperCase()));
            } else {
                mob = EntityType.ZOMBIE;
            }
        }
    }

    @Override
    public AccessType getRequired() {
        return required;
    }

    @Override
    public void setRequired(AccessType required) {
        this.required = required;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public VarType getType() {
        return VarType.STRING;
    }

    @Override
    public boolean compare(String string) {
        return mob.toString().equalsIgnoreCase(string);
    }

    @Override
    public String toString() {
        return mob.toString();
    }

    @Override
    public IField clone(String value) throws Exception {
        MobField mob = new MobField(value, false);
        mob.setRequired(required);
        mob.setComment(comment);
        return mob;
    }

}
