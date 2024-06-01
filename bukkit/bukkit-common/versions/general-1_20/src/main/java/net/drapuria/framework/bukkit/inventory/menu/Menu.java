/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.menu;

import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public abstract class Menu extends AbstractMenu {

    private static final ItemStack AIR = new ItemStack(Material.AIR);


    private final Map<Player, Map<Integer, IButton>> playerButtons = new HashMap<>();
    private final Map<Player, Inventory> inventories = new HashMap<>();

    @Override
    public Map<Integer, IButton> getCachedButtons(Player player) {
        return playerButtons.get(player);
    }

    public void setCachedButtons(Player player, Map<Integer, IButton> map) {
        this.playerButtons.put(player instanceof DrapuriaPlayer ? player.getPlayer() : player, map);
    }

    @Override
    public void openMenu(Player player) {
        final Map<Integer, IButton> buttons = getButtons(player);
        final int size = getSize(player);
        final String title = getTitle(player);
        final Inventory inventory = buildInventory(player, getBukkitInventoryType(player), size, title, buttons);
        setCachedButtons(player, buttons);
        this.inventories.put(player, inventory);
        player.openInventory(inventory);
        MenuService.getService.addOpenedMenu(player.getName(), this);
        onOpen(player);
    }

    @Override
    public Inventory getInventory(Player player) {
        return this.inventories.get(player);
    }

    @Override
    public void removePlayerButtons(Player player) {
        this.playerButtons.remove(player);
    }

    private Inventory buildInventory(final Player player, final InventoryType inventoryType, int size, final String title, final Map<Integer, IButton> buttons) {
        Inventory inventory = null;
        if (size == -1) {
            size = this.size(buttons);
        }
        if (inventories.containsKey(player)) {
            inventory = inventories.get(player);
            if (inventory.getSize() != size)
                inventory = null;
            else
                inventory.setContents(new ItemStack[inventory.getSize()]);
        }

        if (inventory == null) {
            if (inventoryType != null)
                inventory = Bukkit.createInventory(null, inventoryType, Component.text(title));
            else
                inventory = Bukkit.createInventory(null, size, Component.text(title));
        }
        for (Map.Entry<Integer, IButton> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getIcon(player));
        }
        return inventory;
    }

    @Override
    protected void updateButtons(Player player) {
        final Inventory inventory = this.getInventory(player);
        if (inventory != null) {
            inventory.setContents(new ItemStack[inventory.getSize()]);
            Map<Integer, IButton> buttons = getButtons(player);
            playerButtons.put(player, buttons);
            for (Map.Entry<Integer, IButton> entry : buttons.entrySet()) {
                inventory.setItem(entry.getKey(), entry.getValue().getIcon(player));
            }
            player.updateInventory();
        } else
            openMenu(player);
    }

    @Override
    public void updateButton(Player player, int slot, IButton button) {
        final Inventory inventory = this.getInventory(player);
        playerButtons.get(player).put(slot, button);
        inventory.setItem(slot, button.getIcon(player));
    }

    @Override
    public void removeButton(Player player, int slot) {
        final Inventory inventory = this.getInventory(player);
        inventory.setItem(slot, AIR);
        playerButtons.get(player).remove(slot);
    }

    @Override
    public Set<Player> getPlayersInMenu() {
        return this.playerButtons.keySet();
    }

}