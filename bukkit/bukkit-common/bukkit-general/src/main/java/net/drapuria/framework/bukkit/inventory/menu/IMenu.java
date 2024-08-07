/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public interface IMenu {

    String getTitle(Player player);

    void openMenu(Player player);

    int getDefaultSize();

    void setDefaultSize(int size);

    int getSize();

    int getSize(Player player);

    InventoryType getBukkitInventoryType(Player player);

    InventoryType getBukkitInventoryType();

    void setSize(int size);

    void setClosedByMenu(boolean closedByMenu);

    boolean isClosedByMenu();

    boolean allowOwnInventoryClick();

    boolean allowOutsideRightOrLeftClick();

    void setAllowOutsideRightOrLeftClick(boolean allowOutsideRightOrLeftClick);

    void setAllowOwnInventoryClick(boolean allowOwnInventoryClick);

    boolean isAcceptNewItems();

    void setAcceptNewItems(boolean acceptNewItems);

    boolean acceptItemRemove();

    boolean onItemInsert(final Player player, final ItemStack itemStack, int slot);

    boolean onItemRemove(final Player player, final ItemStack itemStack, int slot);

    void setAcceptItemRemove(boolean acceptItemRemove);

    void removePlayerButtons(final Player player);

    void onOpen(Player player);

    void onClose(Player player);

    Inventory getInventory(Player player);

    Map<Integer, IButton> getButtons(Player player);

    Map<Integer, IButton> getCachedButtons(Player player);

    MenuUpdatePolicy getUpdatePolicy(Player player);

    void updateMenu(Player player);

    void updateButton(Player player, int slot, IButton button);

    void removeButton(Player player, int slot);

    Set<Player> getPlayersInMenu();

    default int size(Map<Integer, IButton> buttons) {
        if (this.getSize() != -1) return getDefaultSize();
        int highest = buttons.keySet().stream().mapToInt(buttonValue -> buttonValue).filter(buttonValue -> buttonValue >= 0)
                .max()
                .orElse(9);
        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }


    static int getSlot(int x, int y) {
        return ((9 * y) + x);
    }
}
