/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu.buttons;

import net.drapuria.framework.bukkit.inventory.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class DisplayButton extends Button {

    private final ItemStack displayItem;
    private final boolean shouldCancel;

    public DisplayButton(ItemStack displayItem, boolean shouldCancel) {
        this.displayItem = displayItem;
        this.shouldCancel = shouldCancel;
    }

    @Override
    public ItemStack getIcon(Player player) {
        return this.displayItem;
    }

    @Override
    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return shouldCancel;
    }
}
