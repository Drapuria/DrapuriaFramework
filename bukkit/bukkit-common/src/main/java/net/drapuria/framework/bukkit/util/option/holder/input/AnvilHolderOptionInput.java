package net.drapuria.framework.bukkit.util.option.holder.input;


import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.util.VirtualAnvil;
import net.drapuria.framework.bukkit.util.option.holder.AbstractHolderOption;
import org.bukkit.entity.Player;

public abstract class AnvilHolderOptionInput<V, H, O extends AbstractHolderOption<V, H>> extends AbstractHolderOptionInput<V, H, O> {
    @Override
    public <O1 extends AbstractHolderOption<?, ?>> void startInput(Player player, O1 option) {
        new VirtualAnvil(player, option.getDisplayAdapter().getInputPromptText(), option.getDisplayAdapter().getDisplayItem()) {
            @Override
            public void onConfirm(String str) {
                if (!option.validateInput(str)) {
                    String wrongInput = option.getWrongInputString();
                    if (wrongInput != null)
                        player.sendMessage(wrongInput);
                    return;
                }
                setConfirmedSuccessfully(true);
                option.setValueByPlayer(player, str);
                player.closeInventory();
                AnvilHolderOptionInput.super.endInput(player, true, option);
                onEnd(player);
            }

            @Override
            public void onCancel() {
                if (!(isConfirmedSuccessfully())) {
                    DrapuriaCommon.TASK_SCHEDULER.runSync(() -> {
                        AnvilHolderOptionInput.super.endInput(player, false, option);
                        onEnd(player);
                    });
                }
            }
        };
    }

    abstract void onEnd(final Player player);
}
