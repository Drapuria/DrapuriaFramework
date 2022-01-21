/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.repository;

import net.drapuria.framework.command.FrameworkCommand;

import java.util.Set;

public interface CommandRepository<C extends FrameworkCommand> {

    Set<C> getCommands();

    void registerCommand(Object source, C command);

    void unregisterCommand(C command);

    C findByName(final String name);

}
