/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.util.option.impl;

import net.drapuria.framework.bukkit.util.option.input.AbstractOptionInput;
import net.drapuria.framework.bukkit.util.option.DisplayAdapter;
import net.drapuria.framework.bukkit.util.option.OptionContext;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class SwitchableOptionContext<C, T> extends OptionContext<T, C, T> {

    private List<T> inputList;
    private int selectedIndex;
    private List<String> namedOptions;

    public SwitchableOptionContext(String optionName, C context, Class<T> typeClass, T defaultValue, AbstractOptionInput<C, T, SwitchableOptionContext<C, T>> optionHandler, List<T> inputList, List<String> namedOptions) {
        super(optionName, context, typeClass, defaultValue, optionHandler);
        this.selectedIndex = defaultValue == null ? 0 : inputList.indexOf(defaultValue);
        this.inputList = inputList;
        this.namedOptions = namedOptions;
        setDisplayAdapter();
    }

    private void setDisplayAdapter() {
        setDisplayAdapter(new DisplayAdapter<OptionContext<T, C, T>>(this, getDisplayAdapter().getDisplayName(), getDisplayAdapter().getDisplayItem(), getDisplayAdapter().getDescription()) {
            @Override
            public ItemStack generateDisplayItem() {
                ItemStack display = getDisplayItem().clone();
                ItemMeta displayMeta = display.getItemMeta();
                displayMeta.setDisplayName(getDisplayName());
                List<String> lore = new ArrayList<>();
                if(getDescription() != null) {
                    for(String descLine : getDescription())
                        lore.add("§7§o" + descLine);
                }
                lore.add("");
                for(int i = 0; i < getInputList().size(); i++) {
                    String input = namedOptions.get(i);
                    lore.add("§8■ §7" + input + (getSelectedIndex() == i ? " §8(§aAusgewählt§8)" : ""));
                }
                lore.add("");
                lore.add("§eKlicke, um die Auswahl zu ändern.");
                displayMeta.setLore(lore);
                display.setItemMeta(displayMeta);
                return display;
            }
        });
    }

    @Override
    public boolean validateInput(T input) {
        return true;
    }

    @Override
    public void inputValue(T input) {
        selectedIndex++;
        if(selectedIndex >= inputList.size())
            selectedIndex = 0;
        setValue(inputList.get(selectedIndex));
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public List<T> getInputList() {
        return inputList;
    }

    public List<String> getNamedOptions() {
        return namedOptions;
    }

    @Override
    public void deserializeAndSet(String base64Enc) {
        super.deserializeAndSet(base64Enc);
        selectedIndex = inputList.indexOf(getValue());
    }
}
