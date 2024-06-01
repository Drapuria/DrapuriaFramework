/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.meta;

import lombok.Getter;
import net.drapuria.framework.command.context.permission.PermissionContext;
import net.drapuria.framework.command.executor.ExecutorData;
import net.drapuria.framework.command.parameter.Parameter;
import net.drapuria.framework.command.parameter.ParameterData;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Getter
public abstract class SubCommandMeta<E, P extends Parameter, D extends ExecutorData<E, P>, T extends ParameterData<P>> {

    protected final CommandMeta<E, P, D> commandMeta;
    protected final D executorData;
    private final List<String> labels;

    protected boolean asyncExecution;

    public SubCommandMeta(CommandMeta<E, P, D> commandMeta, D executorData, Set<String> labels) {
        this.commandMeta = commandMeta;
        this.executorData = executorData;
        this.labels = new ArrayList<>(labels);
        this.labels.replaceAll(String::toLowerCase);
    }

    public boolean isValidLabel(final String label) {
        if (this.labels.isEmpty())
            return true;
        return this.labels.contains(label);
    }


    public abstract boolean canAccess(E executor);

}
