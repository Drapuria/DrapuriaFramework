package net.drapuria.framework.bukkit.util.option.impl;

import net.drapuria.framework.bukkit.util.option.DisplayAdapter;
import net.drapuria.framework.bukkit.util.option.OptionContext;
import net.drapuria.framework.bukkit.util.option.input.AbstractOptionInput;
import net.drapuria.framework.util.TimeUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TimeOptionContext<C> extends OptionContext<Long, C, String> {

    public TimeOptionContext(String optionName, C context, Long defaultValue, AbstractOptionInput<C, String, ? extends OptionContext<?, C, String>> inputHandler) {
        super(optionName, context, Long.class, defaultValue, inputHandler);
        setDisplayAdapter();
    }

    private void setDisplayAdapter() {
        setDisplayAdapter(new DisplayAdapter<OptionContext<Long, C, String>>(this, getDisplayAdapter().getDisplayName(), getDisplayAdapter().getDisplayItem(), getDisplayAdapter().getDescription()) {
            @SuppressWarnings("DuplicatedCode")
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
                lore.add("§8■ §7Aktuell: §f" + (isSet() ? TimeUtil.timeToString(getValue(), true) : (getDefaultValue() == null
                        ? "Nicht gesetzt" : TimeUtil.timeToString(getDefaultValue(), true))));
                lore.add("§8■ §7Lesbar: §f" + (isSet() ? TimeUtil.timeToString(getValue(), false) : (getDefaultValue() == null
                        ? "Nicht gesetzt" : TimeUtil.timeToString(getDefaultValue(), false))));
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
        return TimeUtil.parseTime(input) != -1;
    }

    @Override
    public void inputValue(String input) {
        try {
            setValue(Long.parseLong(input));
        } catch (Exception e) {
            setValue(TimeUtil.parseTime(input));
        }
    }
}
