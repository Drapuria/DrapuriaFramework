/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.deprecatedmenu.pageable;

import net.drapuria.framework.bukkit.inventory.deprecatedmenu.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PaginatedButton extends Button {

    private ItemStack icon;

    protected PaginatedButton(ItemStack icon) {
        this.icon = icon;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }
}
