/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.util.option.impl;

import net.drapuria.framework.bukkit.util.option.DisplayAdapter;
import net.drapuria.framework.bukkit.util.option.OptionContext;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDOptionContext<C> extends OptionContext<UUID, C, String> {
    public UUIDOptionContext(String optionName, C context, Class<UUID> typeClass) {
        super(optionName, context, typeClass);
        setDisplayAdapter();
    }

    private void setDisplayAdapter() {
        setDisplayAdapter(new DisplayAdapter<OptionContext<UUID, C, String>>(this, getDisplayAdapter().getDisplayName(), getDisplayAdapter().getDisplayItem(), getDisplayAdapter().getDescription()) {
            @Override
            public ItemStack generateDisplayItem() {
                ItemStack display = getDisplayItem().clone();
                ItemMeta displayMeta = display.getItemMeta();
                displayMeta.setDisplayName(getDisplayName());
                List<String> lore = new ArrayList<>();
                lore.add("§7§oZeitformat: s, m, h, d, mo");
                lore.add("§7§oBeispiel: 30d (30 Tage)");
                if(getDescription() != null) {
                    for(String descLine : getDescription())
                        lore.add("§7§o" + descLine);
                }
                lore.add("");
                lore.add("§8■ §7Aktuell: §f" + (isSet() ? getValue().toString() : (getDefaultValue() == null
                        ? "Nicht gesetzt" : getValue().toString())));
                lore.add("");
                lore.add("§eKlicke, um den Wert zu ändern.");
                displayMeta.setLore(lore);
                display.setItemMeta(displayMeta);
                return display;
            }
        });
    }

    @Override
    public boolean validateInput(String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void inputValue(String input) {
        setValue(UUID.fromString(input));
    }
}
