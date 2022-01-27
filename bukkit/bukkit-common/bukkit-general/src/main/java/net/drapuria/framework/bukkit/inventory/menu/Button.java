/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu;

import net.drapuria.framework.bukkit.inventory.menu.buttons.DisplayButton;
import net.drapuria.framework.bukkit.item.ItemConstants;

public abstract class Button extends AbstractButton {

    public static Button placeholder() {
        return new DisplayButton(ItemConstants.PLACEHOLDER, true);
    }

}
