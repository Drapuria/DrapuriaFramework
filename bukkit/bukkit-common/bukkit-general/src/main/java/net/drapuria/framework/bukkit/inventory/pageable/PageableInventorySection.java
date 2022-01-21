/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.inventory.pageable;

import net.drapuria.framework.pageable.section.PageableGuiSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class PageableInventorySection<I> extends PageableGuiSection<I, Inventory, ItemStack> {
    /**
     * @param gui the gui we are working in
     */
    public PageableInventorySection(@NotNull Inventory gui) {
        super(gui);
    }
}
