/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu.factory;

import net.drapuria.framework.bukkit.inventory.menu.IButton;
import net.drapuria.framework.bukkit.inventory.menu.IMenu;
import net.drapuria.framework.bukkit.inventory.menu.MenuUpdatePolicy;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public abstract class AbstractMenuFactory<F extends AbstractMenuFactory<F, B>, B extends IButton> {

    protected Function<Player, String> title;
    protected Function<Player, Integer> size;
    protected Function<Player, InventoryType> inventoryType;
    protected boolean allowOwnInventoryClick;
    protected boolean allowOutsideRightOrLeftClick;
    protected boolean acceptItemRemove;
    protected boolean acceptItemInsert;
    protected MenuUpdatePolicy updatePolicy;
    protected int defaultSize;

    protected final Map<Integer, Function<Player, B>> buttons = new HashMap<>();


    public F title(Function<Player, String> title) {
        this.title = title;
        return (F) this;
    }

    public F allowOwnInventoryClick(boolean allowOwnInventoryClick) {
        this.allowOwnInventoryClick = allowOwnInventoryClick;
        return (F) this;
    }

    public F acceptItemRemove(boolean acceptItemRemove) {
        this.acceptItemInsert = acceptItemRemove;
        return (F) this;
    }

    public F acceptItemInsert(boolean acceptItemInsert) {
        this.acceptItemInsert = acceptItemInsert;
        return (F) this;
    }

    public F defaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
        return (F) this;
    }

    public F updatePolicy(MenuUpdatePolicy updatePolicy) {
        this.updatePolicy = updatePolicy;
        return (F) this;
    }

    public F allowOutsideRightOrLeftClick(boolean allowOutsideRightOrLeftClick) {
        this.allowOutsideRightOrLeftClick = allowOutsideRightOrLeftClick;
        return (F) this;
    }

    public F size(Function<Player, Integer> size) {
        this.size = size;
        return (F) this;
    }

    public F inventoryType(Function<Player, InventoryType> inventoryType) {
        this.inventoryType = inventoryType;
        return (F) this;
    }

    public F button(Integer slot, Function<Player, B> button) {
        buttons.put(slot, button);
        return (F) this;
    }

    public abstract IMenu buildMenu();


}
