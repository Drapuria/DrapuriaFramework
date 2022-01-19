package net.drapuria.framework.bukkit.inventory.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;


public abstract class Menu extends AbstractMenu {

    private final Map<Player, Map<Integer, IButton>> playerButtons = new HashMap<>();
    private final Map<Player, Inventory> inventories = new HashMap<>();

    @Override
    public Map<Integer, IButton> getCachedButtons(Player player) {
        return playerButtons.get(player);
    }

    public void setCachedButtons(Player player, Map<Integer, IButton> map) {
        this.playerButtons.put(player, map);
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
                inventory = Bukkit.createInventory(null, inventoryType, title);
            else
                inventory = Bukkit.createInventory(null, size, title);
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
}