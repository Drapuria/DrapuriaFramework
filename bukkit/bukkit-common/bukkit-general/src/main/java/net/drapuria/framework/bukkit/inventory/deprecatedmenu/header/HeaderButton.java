/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.deprecatedmenu.header;


import net.drapuria.framework.bukkit.inventory.header.BukkitHeaderIcon;
import net.drapuria.framework.bukkit.inventory.deprecatedmenu.Button;
import net.drapuria.framework.bukkit.util.DrapuriaSoundDefaults;
import net.drapuria.framework.header.controller.HeaderController;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class HeaderButton<H> extends Button {

    private final HeaderController<H> headerController;
    private final BukkitHeaderIcon<H> headerIcon;


    public HeaderButton(HeaderController<H> headerController, BukkitHeaderIcon<H> headerIcon) {
        this.headerController = headerController;
        this.headerIcon = headerIcon;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return headerIcon.getItem(headerController.getSelected() == headerIcon.getAssignedTo());
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        if (headerController.getSelected() == headerIcon.getAssignedTo()) {
            DrapuriaSoundDefaults.STICK_SOUND.play(player);
            return;
        }
        playNeutral(player);
        headerController.setSelected(headerIcon.getAssignedTo());
    }

    @Override
    public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
        return true;
    }
}
