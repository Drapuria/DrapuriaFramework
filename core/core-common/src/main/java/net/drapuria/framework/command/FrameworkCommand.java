/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command;

import net.drapuria.framework.command.meta.CommandMeta;

public interface FrameworkCommand<M extends CommandMeta> {

    String getName();

    M getCommandMeta();

}
