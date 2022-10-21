/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter;

import net.drapuria.framework.command.parameter.ParameterData;

public class BukkitParameterData extends ParameterData<BukkitParameter> {
    public BukkitParameterData(BukkitParameter[] parameters) {
        super(parameters);
    }

    public boolean matches(final String input) {
        String[] args = input.split(" ");
        System.out.println("xxx " + input);
        System.out.println("xxx " + args.length);
        return false;
    }

}
