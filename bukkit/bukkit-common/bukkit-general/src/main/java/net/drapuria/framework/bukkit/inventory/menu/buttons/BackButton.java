/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu.buttons;

import lombok.AllArgsConstructor;
import net.drapuria.framework.bukkit.inventory.menu.Button;
import net.drapuria.framework.bukkit.inventory.menu.IMenu;
import net.drapuria.framework.bukkit.item.ItemConstants;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;



@AllArgsConstructor
public class BackButton extends Button {

    private IMenu back;

    @Override
    public ItemStack getIcon(Player player) {
        ItemStack itemStack = ItemConstants.BACK_BUTTON;
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName("§cZurück");
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void onClick(Player player, int i, ClickType clickType, int hb) {
        playNeutral(player);
        back.openMenu(player);
    }

}
