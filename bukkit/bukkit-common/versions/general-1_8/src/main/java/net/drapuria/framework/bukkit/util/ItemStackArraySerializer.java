/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.util;

import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.inventory.ItemStack;

@Component
public class ItemStackArraySerializer implements ObjectSerializer<ItemStack[], String> {

    @Override
    public String serialize(ItemStack[] itemStacks) {
        return ItemStackSerializer.toBase64List(itemStacks);
    }

    @Override
    public ItemStack[] deserialize(String o) {
        return ItemStackSerializer.fromBase64List(o);
    }

    @Override
    public Class<ItemStack[]> inputClass() {
        return ItemStack[].class;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }
}