/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu.buttons;

import lombok.AllArgsConstructor;
import net.drapuria.framework.bukkit.inventory.deprecatedmenu.Button;
import net.drapuria.framework.bukkit.inventory.deprecatedmenu.Menu;
import net.drapuria.framework.bukkit.item.ItemConstants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@AllArgsConstructor
public class BackButton extends Button {

    private Menu back;

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack itemStack = ItemConstants.BACK_BUTTON;
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName("§cZurück");
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hb) {
        Button.playNeutral(player);
        this.back.openMenu(player);
    }

}
