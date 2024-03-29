/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class SharedMenu extends AbstractSharedMenu {

    @Override
    public void openMenu(Player player) {
        if (inventory == null)
            buildInventory();
        player.openInventory(inventory);
        MenuService.getService.addOpenedMenu(player.getName(), this);
        onOpen(player);
    }

    private void buildInventory() {
        final Map<Integer, IButton> buttons = getButtons();
        if (this.getSize() == -1)
            this.setSize(size(buttons));
        if (getBukkitInventoryType() != null)
            this.inventory = Bukkit.createInventory(null, this.getBukkitInventoryType(), this.getTitle());
        else
            this.inventory = Bukkit.createInventory(null, this.getSize(), this.getTitle());

        for (Map.Entry<Integer, IButton> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getIcon(null));
        }
    }

    @Override
    public void updateButtons() {
        if (inventory == null)
            throw new NullPointerException("inventory is not initialized");
        final Map<Integer, IButton> buttons = getButtons();
        inventory.setContents(new ItemStack[inventory.getSize()]);
        for (Map.Entry<Integer, IButton> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getIcon(null));
        }
    }

    @Override
    public void updateButton(Player player, int slot, IButton button) {
        final Inventory inventory = this.getInventory(player);
        inventory.setItem(slot, button.getIcon(player));
    }
}