/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.item.skull;

import net.drapuria.framework.repository.InMemoryRepository;
import org.bukkit.inventory.ItemStack;

public abstract class SkullRepository extends InMemoryRepository<ItemStack, String> {
    @Override
    public void init() {

    }

    @Override
    public Class<?> type() {
        return ItemStack.class;
    }
}
