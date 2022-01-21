/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.deprecatedmenu;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

@Component
public class ButtonListener implements Listener {

    private final Plugin plugin = Drapuria.PLUGIN;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onButtonClick(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        Menu openedMenu = Menu.getCurrentlyOpenedMenus.get(player);
        if (openedMenu != null) {
            event.setCancelled(true);
            final ClickType clickType = event.getClick();
            final int slot = event.getSlot();
            if (event.getSlot() != event.getRawSlot()) {
                if (clickType.isShiftClick()) {
                    event.setCancelled(true);
                }
                return;
            }
            final Button clickedButton = openedMenu.getPlayerButtons().get(player).get(slot);
            if (clickedButton == null) {
                event.setCancelled(true);
                return;
            }
            final boolean cancel = clickedButton.shouldCancel(player, slot, clickType);
            if (!cancel && (clickType.isShiftClick())) {
                event.setCancelled(true);
                if (event.getCurrentItem() != null)
                    player.getInventory().addItem(event.getCurrentItem());
            } else {
                event.setCancelled(true);
            }
            clickedButton.clicked(player, slot, clickType, event.getHotbarButton());
            final boolean shouldUpdate = clickedButton.shouldUpdate(player, slot, clickType);

            if (Menu.getCurrentlyOpenedMenus.containsKey(player)) {
                Menu newMenu = Menu.getCurrentlyOpenedMenus.get(player);
                if (newMenu == openedMenu) {
                    if (newMenu.isUpdateAfterClick() && shouldUpdate) {
                        openedMenu.setClosedByMenu(true);
                        newMenu.openMenu(player);
                    }
                }
            } else if (shouldUpdate) {
                openedMenu.setClosedByMenu(true);
                openedMenu.openMenu(player);
            }
            // evtl inv updaten?
        }
    }

    @EventHandler(priority =  EventPriority.HIGH)
    public void onInventoryClose(final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        Menu openedMenu = Menu.getCurrentlyOpenedMenus.get(player);
        if (openedMenu != null) {
            openedMenu.onClose(player);
            openedMenu.removePlayerButtons(player);
            Menu.getCurrentlyOpenedMenus.remove(player);

            player.setMetadata("menuglitch", new FixedMetadataValue(plugin, true));
        }
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (player.hasMetadata("menuglitch")) {
            player.removeMetadata("menuglitch", plugin);
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null) {
                    final ItemMeta meta = itemStack.getItemMeta();
                    if (meta != null && meta.hasDisplayName()) {
                        if (meta.getDisplayName().contains("§a§a§a§a§e"))
                            player.getInventory().remove(itemStack);
                    }
                }
            }
        }
    }

}
