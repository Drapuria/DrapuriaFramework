/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public abstract class AbstractMenu implements IMenu {

    private boolean updateAfterClick;
    private boolean hasPlaceholder;
    private boolean isClosedByMenu;
    private boolean allowOwnInventoryClick;
    private boolean acceptNewItems = false;
    private boolean acceptItemRemove = false;
    private boolean allowOutsideRightOrLeftClick = false;

    private InventoryType bukkitInventoryType;

    private int defaultSize = -1;
    private int size = -1;

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int getDefaultSize() {
        return this.defaultSize;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    public void setBukkitInventoryType(InventoryType bukkitInventoryType) {
        this.bukkitInventoryType = bukkitInventoryType;
    }

    @Override
    public InventoryType getBukkitInventoryType() {
        return this.bukkitInventoryType;
    }

    public void setDefaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
    }

    @Override
    public void setClosedByMenu(boolean closedByMenu) {
        this.isClosedByMenu = closedByMenu;
    }

    @Override
    public boolean isClosedByMenu() {
        return this.isClosedByMenu;
    }

    @Override
    public void setAcceptNewItems(boolean acceptNewItems) {
        this.acceptNewItems = acceptNewItems;
    }

    @Override
    public boolean isAcceptNewItems() {
        return acceptNewItems;
    }

    @Override
    public void setAcceptItemRemove(boolean acceptItemRemove) {
        this.acceptItemRemove = acceptItemRemove;
    }

    @Override
    public boolean acceptItemRemove() {
        return false;
    }

    @Override
    public boolean allowOutsideRightOrLeftClick() {
        return this.allowOutsideRightOrLeftClick;
    }

    @Override
    public void setAllowOutsideRightOrLeftClick(boolean allowOutsideRightOrLeftClick) {
        this.allowOutsideRightOrLeftClick = allowOutsideRightOrLeftClick;
    }

    @Override
    public boolean allowOwnInventoryClick() {
        return this.allowOwnInventoryClick;
    }

    @Override
    public void setAllowOwnInventoryClick(boolean allowOwnInventoryClick) {
        this.allowOwnInventoryClick = allowOwnInventoryClick;
    }

    @Override
    public void onClose(Player player) {

    }

    @Override
    public void onOpen(Player player) {

    }

    public Inventory getCurrentInventory(final Player player) {
        return player.getOpenInventory() == null ? null : player.getOpenInventory().getTopInventory();
    }

    @Override
    public MenuUpdatePolicy getUpdatePolicy(Player player) {
        return MenuUpdatePolicy.ALL;
    }

    @Override
    public void updateMenu(Player player) {
        if (getUpdatePolicy(player) == MenuUpdatePolicy.ALL)
            openMenu(player);
        else
            updateButtons(player);
    }

    protected abstract void updateButtons(Player player);

}