package net.drapuria.framework.bukkit.util.option.impl;

import net.drapuria.framework.bukkit.util.option.DisplayAdapter;
import net.drapuria.framework.bukkit.util.option.OptionContext;
import net.drapuria.framework.bukkit.util.option.input.AbstractOptionInput;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StringOptionContext<C> extends OptionContext<String, C, String> {

    public StringOptionContext(String optionName, C context) {
        super(optionName, context, String.class);
        setDisplayAdapter();
    }

    public StringOptionContext(String optionName, C context, String defaultValue) {
        super(optionName, context, String.class, defaultValue);
        setDisplayAdapter();
    }

    public StringOptionContext(String optionName, C context, String defaultValue, AbstractOptionInput<C, String, StringOptionContext<C>> inputHandler) {
        super(optionName, context, String.class, defaultValue, inputHandler);
        setDisplayAdapter();
    }

    private void setDisplayAdapter() {
        setDisplayAdapter(new DisplayAdapter<OptionContext<String, C, String>>(this, getDisplayAdapter().getDisplayName(), getDisplayAdapter().getDisplayItem(), getDisplayAdapter().getDescription()) {
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
                lore.add("§8■ §7Aktuell: §f" + (isSet() ? getValue() : (getDefaultValue() == null ? "Nicht gesetzt" : getDefaultValue())));
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
        return true;
    }

    @Override
    public void inputValue(String input) {
        setValue(input);
    }
}
