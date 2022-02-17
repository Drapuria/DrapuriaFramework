/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu.buttons;

import net.drapuria.framework.bukkit.inventory.menu.Button;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NextPageButton extends Button {
    @Override
    public ItemStack getIcon(Player player) {
        return new ItemBuilder(Material.ARROW)
                .setDisplayName("§7Weiter")
                .build();
    }
}
