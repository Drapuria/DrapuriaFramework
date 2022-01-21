/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.proxy.util.option.impl;


import net.drapuria.framework.proxy.util.option.OptionContext;

import java.util.UUID;

public class UUIDOptionContext<C> extends OptionContext<UUID, C, String> {
    public UUIDOptionContext(String optionName, C context, Class<UUID> typeClass) {
        super(optionName, context, typeClass);
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
