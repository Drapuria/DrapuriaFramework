package net.drapuria.framework.bukkit.inventory.menu.factory;

import net.drapuria.framework.bukkit.inventory.menu.Button;
import net.drapuria.framework.bukkit.inventory.menu.IMenu;
import net.drapuria.framework.bukkit.inventory.menu.InternalMenu;

public class MenuFactory extends AbstractMenuFactory<MenuFactory, Button>{
    @Override
    public IMenu buildMenu() {
        return new InternalMenu(buttons, title, size, inventoryType, acceptItemInsert, acceptItemRemove,
                allowOutsideRightOrLeftClick, allowOwnInventoryClick);
    }
}
