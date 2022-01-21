/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.util.option.impl;

import net.drapuria.framework.bukkit.util.option.input.AbstractOptionInput;
import net.drapuria.framework.bukkit.util.option.OptionContext;

import java.util.ArrayList;

public class ArrayListOptionContext<C> extends OptionContext<ArrayList, C, String> {

    public ArrayListOptionContext(String optionName, C context) {
        super(optionName, context, ArrayList.class);
    }

    public ArrayListOptionContext(String optionName, C context, ArrayList defaultValue) {
        super(optionName, context, ArrayList.class, defaultValue);
    }

    public ArrayListOptionContext(String optionName, C context, ArrayList defaultValue, AbstractOptionInput<C, String, StringOptionContext<C>> inputHandler) {
        super(optionName, context, ArrayList.class, defaultValue, inputHandler);
    }

    @Override
    public boolean validateInput(String input) {
        return true;
    }

    @Override
    public void inputValue(String input) {
        ArrayList<String> list = new ArrayList<>();
        list.add(input);
        setValue(list);
    }
}
