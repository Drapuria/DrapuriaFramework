/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public abstract class AbstractSharedMenu extends AbstractMenu implements ISharedMenu {

    protected Inventory inventory;

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        return null;
    }

    @Override
    protected void updateButtons(Player player) {
        updateButtons();
    }

    @Override
    public int getSize(Player player) {
        return -1;
    }

    @Override
    public InventoryType getBukkitInventoryType(Player player) {
        return null;
    }

    @Override
    public String getTitle(Player player) {
        return null;
    }
}