package net.rapust.starspaceblocks.data.action.type;

import lombok.Data;
import net.rapust.starspaceblocks.data.action.IAction;
import net.rapust.starspaceblocks.data.action.ActionType;
import net.rapust.starspaceblocks.data.action.Value;
import net.rapust.starspaceblocks.util.PlaceholderUtil;
import net.rapust.starspaceblocks.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Data
public class TitlePlayerAction implements IAction {

    private String name;
    private Value value;

    private String title;
    private String subTitle;

    private final Boolean setup;

    public TitlePlayerAction(String name, Value value, Boolean setup) {
        this.name = name;
        this.setup = setup;
        setValue(value);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ActionType getType() {
        return ActionType.SEND_TITLE_TO_PLAYER;
    }

    @Override
    public void setValue(Value value) {
        this.value = value;

        try {
            String[] values = value.getUnformattedString().split("<~>");

            if (values.length == 1) {
                title = ChatColor.translateAlternateColorCodes('&', values[0]);
                subTitle = "";
            } else {
                title = ChatColor.translateAlternateColorCodes('&', values[0]);
                subTitle = ChatColor.translateAlternateColorCodes('&', values[1]);
            }
        } catch (Exception exception) {
            if (!setup) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public void perform(Player player) throws Exception {
        PlayerUtil.checkPlayer(player);
        player.sendTitle(PlaceholderUtil.format(player, title), PlaceholderUtil.format(player, subTitle));
    }

}
