/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.service;

import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.beans.annotation.PostDestroy;
import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.command.provider.CommandProvider;

import java.lang.annotation.Annotation;

@Service(name = "CommandService")
public class CommandService {

    public static CommandService INSTANCE;

    private CommandProvider<?, ?> commandProvider;

    private Class<? extends Annotation> commandAnnotation, subCommandAnnotation, parameterAnnotation;

    @PreInitialize
    private void preInit() {
        INSTANCE = this;
        FrameworkMisc.PLATFORM.registerCommandProvider();
        commandProvider.registerDefaults();
    }

    @PostInitialize
    public void init() {

    }

    @PostDestroy
    public void shutdown() {
        if (commandProvider != null)
            commandProvider.shutdown();
    }

    public void registerCommandProvider(CommandProvider<?, ?> commandProvider) {
        if (this.commandProvider != null) {
            DrapuriaCommon.getLogger().error("[Drapuria] Command Provider already registered.");
            return;
        }
        this.commandProvider = commandProvider;
    }


    public void setSubCommandAnnotation(Class<? extends Annotation> subCommandAnnotation) {
        this.subCommandAnnotation = subCommandAnnotation;
    }

    public void setCommandAnnotation(Class<? extends Annotation> commandAnnotation) {
        this.commandAnnotation = commandAnnotation;
    }

    public void setParameterAnnotation(Class<? extends Annotation> parameterAnnotation) {
        this.parameterAnnotation = parameterAnnotation;
    }

    public Class<? extends Annotation> getCommandAnnotation() {
        return commandAnnotation;
    }

    public Class<? extends Annotation> getSubCommandAnnotation() {
        return subCommandAnnotation;
    }

    public Class<? extends Annotation> getParameterAnnotation() {
        return parameterAnnotation;
    }

    public CommandProvider<?, ?> getCommandProvider() {
        return commandProvider;
    }
}
