/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter;

import net.drapuria.framework.command.parameter.ParameterData;

import java.lang.reflect.Method;
import java.util.Set;

public class BukkitParameterData extends ParameterData<BukkitParameter> {


    public BukkitParameterData(BukkitParameter[] parameters) {
        super(parameters);
    }
}