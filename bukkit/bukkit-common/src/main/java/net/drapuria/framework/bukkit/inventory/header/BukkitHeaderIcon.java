package net.drapuria.framework.bukkit.inventory.header;

import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.drapuria.framework.header.HeaderIcon;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BukkitHeaderIcon<H> extends HeaderIcon<H, ItemStack> {

    static {
        getClickDescriptionTemplate = new String[]{ "", "§3Klicke§7, um diese Kategorie zu §a§ffnen§7." };
    }


    public BukkitHeaderIcon(H assignedTo, ItemStack icon, String displayName, String[] description, String viewPermission) {
        super(assignedTo, icon, displayName, description, viewPermission);
    }

    public BukkitHeaderIcon(H assignedTo, ItemStack icon, String displayName, String[] description, String[] focusedDescription, String viewPermission) {
        super(assignedTo, icon, displayName, description, focusedDescription, viewPermission);
    }

    public BukkitHeaderIcon(H assignedTo, ItemStack icon, String displayName, String[] description, String[] focusedDescription, String[] clickDescription, String viewPermission) {
        super(assignedTo, icon, displayName, description, focusedDescription, clickDescription, viewPermission);
    }

    public BukkitHeaderIcon(H assignedTo, ItemStack icon, ItemStack focussedIcon, ItemStack unfocusedIcon, String displayName, String[] description, String[] focusedDescription, String[] clickDescription, String viewPermission) {
        super(assignedTo, icon, focussedIcon, unfocusedIcon, displayName, description, focusedDescription, clickDescription, viewPermission);
    }

    @Override
    public ItemStack buildFocusedItem() {
        ItemBuilder itemBuilder = (new ItemBuilder(getIcon())).setDisplayName(getDisplayName())
                .setLore(getFocusedDescription());
        itemBuilder.setGlow().build();
        return itemBuilder.build();
    }

    @Override
    public ItemStack buildUnfocusedItem() {
        ItemBuilder itemBuilder = (new ItemBuilder(getIcon())).setDisplayName(getDisplayName())
                .setLore(getDescription());
        if (getClickDescription() != null)
            itemBuilder.addLore(Arrays.asList(getClickDescription()));
        return itemBuilder.build();
    }

}
