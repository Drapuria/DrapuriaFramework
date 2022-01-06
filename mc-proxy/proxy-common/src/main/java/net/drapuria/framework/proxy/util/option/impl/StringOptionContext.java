package net.drapuria.framework.proxy.util.option.impl;


import net.drapuria.framework.proxy.util.option.OptionContext;

public class StringOptionContext<C> extends OptionContext<String, C, String> {

    public StringOptionContext(String optionName, C context) {
        super(optionName, context, String.class);
    }

    public StringOptionContext(String optionName, C context, String defaultValue) {
        super(optionName, context, String.class, defaultValue);
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
