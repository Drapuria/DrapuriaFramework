/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter;

import lombok.Getter;
import net.drapuria.framework.command.annotation.NullParameterAction;
import net.drapuria.framework.command.parameter.Parameter;

@Getter
public class BukkitParameter extends Parameter {
    private final String[] tabCompleteFlags;
    public BukkitParameter(Class<?> classType, String parameter, String defaultValue, boolean wildcard, boolean isAllowNull, String[] tabCompleteFlags, java.lang.reflect.Parameter javaParameter, final NullParameterAction nullParameterAction) {
        super(classType, parameter, defaultValue, wildcard, isAllowNull, javaParameter, nullParameterAction);
        this.tabCompleteFlags = tabCompleteFlags;
    }
}
