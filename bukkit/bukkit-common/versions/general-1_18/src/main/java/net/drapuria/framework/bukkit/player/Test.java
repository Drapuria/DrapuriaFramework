package net.drapuria.framework.bukkit.player;

import net.drapuria.framework.bukkit.inventory.deprecatedmenu.Button;
import net.drapuria.framework.bukkit.inventory.menu.IButton;
import net.drapuria.framework.bukkit.inventory.menu.Menu;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Test extends Menu {
    @Override
    public String getTitle(Player player) {
        return "§a§lInventar";
    }

    @Override
    public int getSize(Player player) {
        return 27;
    }

    @Override
    public InventoryType getBukkitInventoryType(Player player) {
        return null;
    }



    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();
        buttons.put(0, new IButton() {
            @Override
            public ItemStack getIcon() {
                return ItemBuilder.of(Material.MAGENTA_STAINED_GLASS).setDisplayName("§a§lHallo")
                        .build();
            }

            @Override
            public ItemStack setIcon(ItemStack inventory) {
                return null;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.sendMessage("HEY");
                new Test2().openMenu(player);
            }
        });
        return buttons;
    }
}
