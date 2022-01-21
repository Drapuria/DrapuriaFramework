/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.util.option.holder.input;


import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.inventory.anvil.AbstractVirtualAnvil;
import net.drapuria.framework.bukkit.inventory.anvil.ConfirmAction;
import net.drapuria.framework.bukkit.util.option.holder.AbstractHolderOption;
import org.bukkit.entity.Player;

public abstract class AnvilHolderOptionInput<V, H, O extends AbstractHolderOption<V, H>> extends AbstractHolderOptionInput<V, H, O> {
    @Override
    public <O1 extends AbstractHolderOption<?, ?>> void startInput(Player player, O1 option) {

        Drapuria.IMPLEMENTATION.createVirtualAnvil(player,
                option.getDisplayAdapter().getInputPromptText(),
                option.getDisplayAdapter().getDisplayItem(), (player1, confirmAction) -> {
                    if (confirmAction != ConfirmAction.CONFIRMED) {
                        DrapuriaCommon.TASK_SCHEDULER.runSync(() -> {
                            AnvilHolderOptionInput.super.endInput(player, false, option);
                            onEnd(player);
                        });
                    }
                }, (player1, text) -> {
                    if (!option.validateInput(text)) {
                        String wrongInput = option.getWrongInputString();
                        if (wrongInput != null)
                            player.sendMessage(wrongInput);
                        return ConfirmAction.DENY;
                    }
                    option.setValueByPlayer(player, text);
                    player.closeInventory();
                    AnvilHolderOptionInput.super.endInput(player, true, option);
                    onEnd(player);
                    return ConfirmAction.CONFIRMED;
                });
    }

    abstract void onEnd(final Player player);
}
