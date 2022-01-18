package net.drapuria.framework.bukkit.util.option.holder.input;

import net.drapuria.framework.bukkit.util.PlayerAction;
import net.drapuria.framework.bukkit.util.option.holder.AbstractHolderOption;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class AbstractHolderOptionInput<V, H, O extends AbstractHolderOption<V, ?>> {

    private static final HashMap<AbstractHolderOption<?, ?>, PlayerAction>[] endActions = new HashMap[2];

    static {
        endActions[0] = new HashMap<>();
        endActions[1] = new HashMap<>();
    }

    public abstract <O extends AbstractHolderOption<?, ?>> void startInput(Player player, O option);

    public <O extends AbstractHolderOption<? extends Object, ? extends Object>> void endInput(Player player, boolean success, O option) {
        if (success) {
            if (endActions[0].containsKey(option)) {
                endActions[0].get(option).action(player);
            }
        } else {
            if (endActions[1].containsKey(option))
                endActions[1].get(option).action(player);
        }
    }

    public static void registerEndAction(AbstractHolderOption<?, ?> option, PlayerAction successAction, PlayerAction failureAction) {
        endActions[0].put(option, successAction);
        endActions[1].put(option, failureAction);
    }

}
