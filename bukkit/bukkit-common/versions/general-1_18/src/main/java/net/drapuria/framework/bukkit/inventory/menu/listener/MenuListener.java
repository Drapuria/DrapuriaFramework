/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu.listener;

import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.inventory.menu.IButton;
import net.drapuria.framework.bukkit.inventory.menu.IMenu;
import net.drapuria.framework.bukkit.inventory.menu.MenuService;
import net.drapuria.framework.bukkit.inventory.menu.MenuUpdatePolicy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.Map;

import static org.bukkit.Material.AIR;
import static org.bukkit.event.inventory.ClickType.SHIFT_LEFT;
import static org.bukkit.event.inventory.ClickType.SHIFT_RIGHT;
import static org.bukkit.event.inventory.InventoryAction.DROP_ALL_CURSOR;
import static org.bukkit.event.inventory.InventoryAction.DROP_ONE_CURSOR;

@Component
public class MenuListener implements Listener {

    private final Plugin plugin = Drapuria.PLUGIN;
    private final MenuService service = MenuService.getService;

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final IMenu currentMenu = service.getOpenedMenu(player.getName());
        if (currentMenu == null)
            return;
        final ClickType clickType = event.getClick();
        final InventoryAction inventoryAction = event.getAction();
        if (!currentMenu.allowOutsideRightOrLeftClick() && (inventoryAction == DROP_ALL_CURSOR || inventoryAction == DROP_ONE_CURSOR)) {
            event.setCancelled(true);
            return;
        }
        final Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null)
            return;

        final Inventory menuInventory = currentMenu.getInventory(player);
        final int slot = event.getRawSlot();
        if (clickedInventory != menuInventory) {
            if (!currentMenu.allowOwnInventoryClick() || clickType == SHIFT_LEFT || clickType == SHIFT_RIGHT)
                event.setCancelled(true);
        }
        if (clickedInventory.equals(menuInventory) && event.getCursor() != null && !currentMenu.isAcceptNewItems())
            event.setCancelled(true);

        final Map<Integer, IButton> buttons = currentMenu.getCachedButtons(player);
        final IButton currentButton = buttons.get(slot);
        if (currentButton == null) {
            if (clickedInventory.equals(menuInventory) && event.getCurrentItem() != null && event.getCurrentItem().getType() != AIR
                    && currentMenu.acceptItemRemove() && !currentMenu.onItemRemove(player, event.getCurrentItem(), event.getRawSlot())) {
                event.setCancelled(true);
            } else if (clickedInventory.equals(menuInventory) && event.getCursor() != null && event.getCursor().getType() != AIR
                    && currentMenu.isAcceptNewItems() && !currentMenu.onItemInsert(player, event.getCursor(), event.getRawSlot())) {
                event.setCancelled(true);
            }
            return;
        }
        if (!event.isCancelled() && currentButton.shouldCancel(player, slot, clickType))
            event.setCancelled(true);

        if (!event.isCancelled() && event.getHotbarButton() != -1) {
            if (!currentMenu.acceptItemRemove())
                event.setCancelled(true);
        }
        currentButton.onClick(player, slot, clickType, event.getHotbarButton());
        final boolean shouldUpdate = currentButton.shouldUpdate(player, slot, clickType);
        if (shouldUpdate) {
            if (currentMenu.getUpdatePolicy(player) == MenuUpdatePolicy.ALL)
                currentMenu.updateMenu(player);
            else
                currentMenu.updateButton(player, slot, currentButton);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final IMenu currentMenu = service.getOpenedMenu(player.getName());
        if (currentMenu == null) return;
        final Inventory menuInventory = currentMenu.getInventory(player);
        final Inventory clickedInventory = event.getInventory();
        final Inventory bottomInventory = event.getView().getBottomInventory();
        if (clickedInventory.equals(menuInventory) && !currentMenu.isAcceptNewItems()) {
            if (clickedInventory.equals(bottomInventory))
                return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final IMenu currentMenu = service.getOpenedMenu(player.getName());
        if (currentMenu != null) {
            currentMenu.onClose(player);
            currentMenu.removePlayerButtons(player);
            service.removePlayer(player.getName());
        }
    }
}
