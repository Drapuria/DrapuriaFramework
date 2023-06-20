/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.provider;

import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.beans.component.ComponentRegistry;
import net.drapuria.framework.beans.details.constructor.BeanParameterDetailsConstructor;
import net.drapuria.framework.command.FrameworkCommand;
import net.drapuria.framework.command.annotation.DefaultPermissionContext;
import net.drapuria.framework.command.context.permission.PermissionContext;
import net.drapuria.framework.command.context.permission.UnknownPermissionContext;
import net.drapuria.framework.command.parser.CommandTypeParameterParser;
import net.drapuria.framework.command.repository.CommandRepository;
import net.drapuria.framework.command.service.CommandService;
import net.drapuria.framework.util.ClasspathScanner;
import net.drapuria.framework.util.TypeAnnotationScanner;

import java.security.CodeSource;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public abstract class CommandProvider<C extends FrameworkCommand<?>, P extends CommandTypeParameterParser<?, ?>> {

    private final CommandRepository<C> commandRepository;
    private final Class<C> type;
    private final Map<Class<?>, P> commandTypeParameterParser;
    private final Map<Class<? extends PermissionContext>, PermissionContext<?>> permissionContextList = new HashMap<>();
    private PermissionContext<?> defaultPermissionContext;

    public CommandProvider(CommandRepository<C> commandRepository, Class<C> type) {
        this.commandRepository = commandRepository;
        this.type = type;
        this.commandTypeParameterParser = new HashMap<>();
        ComponentRegistry.registerComponentHolder(new ComponentHolder() {
            @Override
            public void onEnable(Object instance) {
                System.out.println("PERMISSION CONTEXT ENABLE");
                if (instance.getClass().isAnnotationPresent(DefaultPermissionContext.class)) {
                    System.out.println("IS DEFAULT PERMISSION CONTEXT " + (PermissionContext<?>) instance);
                    System.out.println("not casted = " + instance);
                    CommandProvider.this.defaultPermissionContext = (PermissionContext<?>) instance;
                }
                registerPermissionContext((PermissionContext<?>) instance);
            }
            @Override
            public Class<?>[] type() {
                return new Class[] {PermissionContext.class};
            }
        });
    }

    public void registerCommand(Object source, C command) {
        this.commandRepository.registerCommand(source, command);
    }

    public void unregisterCommand(C command) {
        this.commandRepository.unregisterCommand(command);
    }

    public void registerTypeParameter(P parser) {
        this.commandTypeParameterParser.put(parser.getType(), parser);
    }

    public void unregisterTypeParameterParser(P parser) {
        this.commandTypeParameterParser.remove(parser.getType());
    }

    public abstract void registerDefaults();

    public abstract void shutdown();

    @SuppressWarnings("unchecked")
    protected List<Class<?>> findCommandClasses() {
        final CommandService commandService = (CommandService) DrapuriaCommon.BEAN_CONTEXT.getBean(CommandService.class);
        TypeAnnotationScanner annotationScanner = new TypeAnnotationScanner(ClasspathScanner.getCodeSourceOf(this),
                commandService.getCommandAnnotation());

        return annotationScanner.getResult();
    }
    @SuppressWarnings("unchecked")
    public List<Class<?>> findCommandClasses(CodeSource codeSource, String packageName) {
        final CommandService commandService = (CommandService) DrapuriaCommon.BEAN_CONTEXT.getBean(CommandService.class);
        TypeAnnotationScanner annotationScanner = new TypeAnnotationScanner(codeSource,
                packageName,
                commandService.getCommandAnnotation());

        return annotationScanner.getResult();
    }

    protected BeanParameterDetailsConstructor constructorDetails(Class<?> type) {
        return new BeanParameterDetailsConstructor(type, BeanContext.INSTANCE);
    }

    public void registerPermissionContext(PermissionContext<?> context) {
        this.permissionContextList.put(context.getClass(), context);
    }

    public Optional<PermissionContext> findPermissionContext(Class<? extends PermissionContext> context) {
        if (context == UnknownPermissionContext.class) {
            return Optional.ofNullable(this.defaultPermissionContext);
        }
        return Optional.ofNullable(this.permissionContextList.get(context));
    }

    public void unregisterPermissionContext(PermissionContext context) {
        this.permissionContextList.remove(context.getClass(), context);
    }
}