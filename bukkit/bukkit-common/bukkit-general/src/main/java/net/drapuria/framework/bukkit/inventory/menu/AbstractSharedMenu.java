package net.drapuria.framework.bukkit.inventory.menu;

import org.bukkit.inventory.Inventory;

public abstract class AbstractSharedMenu extends AbstractMenu implements ISharedMenu {

    protected Inventory inventory;

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}