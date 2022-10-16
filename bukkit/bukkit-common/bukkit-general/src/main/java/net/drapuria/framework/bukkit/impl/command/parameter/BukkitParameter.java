/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter;

import lombok.Getter;
import net.drapuria.framework.command.parameter.Parameter;

@Getter
public class BukkitParameter extends Parameter {
    private final String[] tabCompleteFlags;
    public BukkitParameter(Class<?> classType, String parameter, String defaultValue, boolean wildcard, String[] tabCompleteFlags, java.lang.reflect.Parameter javaParameter) {
        super(classType, parameter, defaultValue, wildcard, javaParameter);
        this.tabCompleteFlags = tabCompleteFlags;
    }
}
