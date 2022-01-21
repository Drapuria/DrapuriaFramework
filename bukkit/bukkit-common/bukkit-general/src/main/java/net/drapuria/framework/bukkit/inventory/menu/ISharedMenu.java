/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public interface ISharedMenu extends IMenu {

    Map<Integer, IButton> getButtons();

    Inventory getInventory();

    String getTitle();

    default Map<Integer, IButton> getButtons(Player player) {
        return this.getButtons();
    }

    default Map<Integer, IButton> getCachedButtons(Player player) {
        return this.getButtons();
    }

    default Inventory getInventory(Player player) {
        return this.getInventory();
    }

    default void removePlayerButtons(Player player) {
        ;
    }
}
