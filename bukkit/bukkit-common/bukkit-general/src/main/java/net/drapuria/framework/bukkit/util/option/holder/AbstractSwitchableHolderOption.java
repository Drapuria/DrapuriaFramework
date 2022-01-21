/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.util.option.holder;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSwitchableHolderOption<V, H> extends AbstractHolderOption<V, H> implements SwitchableHolderOption<V, H> {

    private final V[] possibleValues;
    private final Map<H, Integer> indexes = new HashMap<>();
    private final Map<V, Integer> indexCache = new HashMap<>();
    private int defaultIndex;

    public AbstractSwitchableHolderOption(String optionName, Class<V> valueClass, Class<H> holderClass, V[] possibleValues, V defaultOption) {
        super(defaultOption, optionName, valueClass, holderClass);
        this.possibleValues = possibleValues;
        for (int index = 0; index < this.possibleValues.length; index++) {
            if (this.possibleValues[index].equals(defaultOption)) {
                this.defaultIndex = index;
            }
            this.indexCache.put(this.possibleValues[index], index);
        }
    }


    public V getSelected(H holder) {
        return possibleValues[this.indexes.getOrDefault(holder, this.defaultIndex)];
    }

    @Override
    public V getValue(H holder) {
        return this.getSelected(holder);
    }

    @Override
    public void setValue(H holder, V value) {
        this.indexes.put(holder, this.indexCache.getOrDefault(value, 0));
    }

    @Override
    public V switchValues(H holder) {
        if (this.indexes.get(holder) == possibleValues.length - 1)
            this.indexes.put(holder, 0);
        else
            this.indexes.put(holder, this.indexes.getOrDefault(holder, this.defaultIndex) + 1);
        return this.getSelected(holder);
    }

    public void switchValues(Player player) {
        this.switchValues(getHolderFromPlayer(player));
    }

}
