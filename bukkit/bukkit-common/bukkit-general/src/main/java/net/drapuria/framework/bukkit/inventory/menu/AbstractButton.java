package net.drapuria.framework.bukkit.inventory.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public abstract class AbstractButton implements IButton {

    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return true;
    }

    public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
        return false;
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType, int hotbarButton) {}
}
