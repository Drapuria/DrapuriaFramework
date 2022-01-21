/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.deprecatedmenu;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.util.SpigotUtil;
import net.drapuria.framework.bukkit.util.inventory.TitleUpdater;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class Menu {

    public static Map<Player, Menu> getCurrentlyOpenedMenus = new HashMap<>();
    private boolean updateAfterClick, closedByMenu, placeholder;
    private int size = -1;
    protected final Plugin plugin = Drapuria.PLUGIN;

    private final Map<Player, Map<Integer, Button>> playerButtons = new HashMap<>();
    private Button placeholderButton = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, "§a");

    public void removePlayerButtons(Player player) {
        this.playerButtons.remove(player);
    }

    public Map<Integer, Button> getPlayerButtons(Player player) {
        return this.playerButtons.get(player);
    }

    public void setButtons(Player player, Map<Integer, Button> buttons) {
        this.playerButtons.put(player, buttons);
    }

    public void openMenu(final Player player) {
        Map<Integer, Button> buttons = this.getButtons(player);
        setButtons(player, buttons);
        Menu previousMenu = Menu.getCurrentlyOpenedMenus.get(player);
        boolean update = false;
        Inventory inventory = null;
        final int size = this.size(this.getPlayerButtons(player));
        String title = getTitle(player);
        if (SpigotUtil.SPIGOT_TYPE != SpigotUtil.SpigotType.HARDCORE_SPIGOT) {
            if (title.length() > 32)
                title = title.substring(0, 32);
        }
        if (player.getOpenInventory() != null) {
            if (previousMenu == null)
                player.closeInventory();
            else {
                final int previousSize = player.getOpenInventory().getTopInventory().getSize();
                if (previousSize == size) {
                    inventory = player.getOpenInventory().getTopInventory();
                    update = true;
                } else {
                    previousMenu.setClosedByMenu(true);
                    getCurrentlyOpenedMenus.remove(player);
                    player.setMetadata("menuglitch", new FixedMetadataValue(plugin, true));

                }
            }
        }
        if (inventory == null) {
            inventory = Bukkit.createInventory(null, size, title);
        }
        inventory.setContents(new ItemStack[inventory.getSize()]); // better than Inventory#clear
        if (this.isPlaceholder()) {
            final ItemStack placeHolderItem = getPlaceholderButton().getButtonItem(player);
            for (int i = 0; i < inventory.getSize(); i++)
                inventory.setItem(i, placeHolderItem);
        }
        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getButtonItem(player));
        }

        if (update) {
            player.updateInventory();
            TitleUpdater.update(player, title);
        } else
            player.openInventory(inventory);
        getCurrentlyOpenedMenus.put(player, this);
        this.onOpen(player);
        this.setClosedByMenu(false);

    }

    public void updateSlot(final Player player, final Integer slot) {
        InventoryView inventoryView = player.getOpenInventory();
        if (inventoryView != null) {
            if (getCurrentlyOpenedMenus.containsKey(player) && getCurrentlyOpenedMenus.get(player) == this) {
                Map<Integer, Button> buttons = getButtons(player);
                if (buttons.containsKey(slot)) {
                    inventoryView.getTopInventory().setItem(slot, createItemStack(player, buttons.get(slot)));
                }
            }
        }
    }

    private ItemStack createItemStack(Player player, Button button) {
        ItemStack item = button.getButtonItem(player);

        if (item.getType() != Material.SKULL_ITEM) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                meta.setDisplayName(meta.getDisplayName() + "§b§c§d§e");
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public int size(Map<Integer, Button> buttons) {
        if (this.size != -1) return this.size;
        int highest = 0;
        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }
        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }

    protected int getSlot(int x, int y) {
        return ((9 * y) + x);
    }

    public abstract String getTitle(Player player);

    public abstract Map<Integer, Button> getButtons(final Player player);

    public void onOpen(final Player player) {}

    public void onClose(final Player player) {}
}
