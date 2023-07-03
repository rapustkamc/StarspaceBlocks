package net.rapust.starspaceblocks.data.action;

import com.plotsquared.core.plot.Plot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.rapust.starspaceblocks.data.action.type.*;

@Getter
@AllArgsConstructor
public enum ActionType {

    EFFECT("EFFECT", EffectAction.class, false),
    COMMAND("COMMAND", CommandAction.class, false),
    SET_HEALTH("SET-HEALTH", HealthAction.class, false),
    SEND_MESSAGE_TO_PLAYER("SEND-MESSAGE-TO-PLAYER", MessagePlayerAction.class, false),
    SEND_MESSAGE_TO_PLOT("SEND-MESSAGE-TO-PLOT", MessagePlotAction.class, true),
    SEND_TITLE_TO_PLAYER("SEND-TITLE-TO-PLAYER", TitlePlayerAction.class, false),
    SEND_TITLE_TO_PLOT("SEND-TITLE-TO-PLOT", TitlePlotAction.class, true),
    COMMAND_ON_PLOT("COMMAND-ON-PLOT", CommandPlotAction.class, true),
    EFFECT_ON_PLOT("EFFECT-ON-PLOT", EffectPlotAction.class, true),
    SET_HEALTH_ON_PLOT("SET-HEALTH-ON-PLOT", HealthPlotAction.class, true);

    private final String name;
    private final Class<? extends IAction> clazz;
    private final boolean plot;

    public static ActionType getByName(String name) {
        for (ActionType actionType : values()) {
            if (actionType.getName().equalsIgnoreCase(name)) {
                return actionType;
            }
        }
        return null;
    }

    public static ActionType getByAction(IAction action) {
        for (ActionType actionType : values()) {
            if (actionType.getClazz().isInstance(action)) {
                return actionType;
            }
        }
        return null;
    }

    public static IAction parseString(String name, ActionType type, Value value, Boolean setup, Plot plot) throws Exception {
        Class<? extends IAction> clazz = type.getClazz();
        if (type.plot) {
            return clazz.getConstructor(name.getClass(), value.getClass(), Boolean.class, Plot.class).newInstance(name, value, setup, plot);
        }
        return clazz.getConstructor(name.getClass(), value.getClass(), Boolean.class).newInstance(name, value, setup);
    }

}
