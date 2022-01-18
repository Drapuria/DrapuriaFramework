package net.drapuria.framework.bukkit.inventory.menu.buttons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.drapuria.framework.bukkit.inventory.menu.Button;
import net.drapuria.framework.bukkit.inventory.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@AllArgsConstructor
@Getter
public class BackButton extends Button {

    private Menu back;

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack itemStack = new ItemStack(Material.ITEM_FRAME);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName("§cZurück");
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hb) {
        Button.playNeutral(player);
        this.back.openMenu(player);
    }
}
