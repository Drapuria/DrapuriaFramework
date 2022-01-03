package net.drapuria.framework.bungee.util.option.impl;

import net.drapuria.framework.bungee.util.option.OptionContext;
import net.drapuria.framework.util.TimeUtil;

public class TimeOptionContext<C> extends OptionContext<Long, C, String> {

    public TimeOptionContext(String optionName, C context, Long defaultValue) {
        super(optionName, context, Long.class, defaultValue);
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
