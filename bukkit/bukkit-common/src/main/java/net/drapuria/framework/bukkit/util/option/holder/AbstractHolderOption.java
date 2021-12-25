package net.drapuria.framework.bukkit.util.option.holder;

import net.drapuria.framework.bukkit.util.option.holder.input.AbstractHolderOptionInput;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHolderOption<V, H>  {
    private final Map<H, V> values = new HashMap<>();

    private final String optionName;
    private final Class<V> valueClass;
    private final Class<H> holderClass;
    private final V defaultValue;
    private HolderOptionDisplayAdapter<?, H> optionDisplayAdapter;
    private AbstractHolderOptionInput<V, H, ? extends AbstractHolderOption<?, ?>> optionInput;

    public AbstractHolderOption(String optionName, Class<V> valueClass, Class<H> holderClass) {
        this(null, optionName, valueClass, holderClass);
    }

    public AbstractHolderOption(V defaultValue, String optionName, Class<V> valueClass, Class<H> holderClass) {
        this.defaultValue = defaultValue;
        this.optionName = optionName;
        this.valueClass = valueClass;
        this.holderClass = holderClass;
    }
    public void setValue(H holder, V value) {
            this.values.put(holder, value);
    }

    public void setValueByPlayer(Player player, String value) {
        this.values.put(getHolderFromPlayer(player), convertInputFromString(value));
    }


    public V getValue(H holder) {
        return this.values.getOrDefault(holder, defaultValue);
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public void remove(H holder) {
        this.values.remove(holder);
    }


    public void setDisplayAdapter(HolderOptionDisplayAdapter<?, H> optionDisplayAdapter) {
        this.optionDisplayAdapter = optionDisplayAdapter;
    }

    public void inputValue(H holder, V input) {
        setValue(holder, input);
    }

    public HolderOptionDisplayAdapter<?, H> getDisplayAdapter() {
        return optionDisplayAdapter;
    }

    public AbstractHolderOptionInput<V, H, ? extends AbstractHolderOption<?, ?>> getOptionInput() {
        return optionInput;
    }

    public void setOptionInput(AbstractHolderOptionInput<V, H, ? extends AbstractHolderOption<?, ?>> optionInput) {
        this.optionInput = optionInput;
    }

    public void startInput(Player h) {
       // optionInput.startInput(h, this);
    }

    public Class<H> getHolderClass() {
        return holderClass;
    }

    public abstract void setDisplayAdapter();

    public abstract boolean validateInput(String input);

    public abstract V convertInputFromString(String str);

    public abstract H getHolderFromPlayer(Player player);

    public abstract String getWrongInputString();

}
