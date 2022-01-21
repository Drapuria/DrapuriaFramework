/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.proxy.util.option.impl;



import net.drapuria.framework.proxy.util.option.OptionContext;

import java.util.ArrayList;

public class ArrayListOptionContext<C> extends OptionContext<ArrayList, C, String> {

    public ArrayListOptionContext(String optionName, C context) {
        super(optionName, context, ArrayList.class);
    }

    public ArrayListOptionContext(String optionName, C context, ArrayList defaultValue) {
        super(optionName, context, ArrayList.class, defaultValue);
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
