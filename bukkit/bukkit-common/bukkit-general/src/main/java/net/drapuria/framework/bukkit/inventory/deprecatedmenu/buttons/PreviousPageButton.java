package net.drapuria.framework.bukkit.inventory.deprecatedmenu.buttons;

import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.drapuria.framework.bukkit.inventory.deprecatedmenu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PreviousPageButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.ARROW)
                .setDisplayName("§7Zurück")
                .build();
    }
}
