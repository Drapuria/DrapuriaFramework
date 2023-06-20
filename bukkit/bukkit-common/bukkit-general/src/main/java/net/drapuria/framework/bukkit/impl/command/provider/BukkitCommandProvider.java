/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.provider;

import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.beans.annotation.ClasspathScan;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.annotation.UseFrameworkPlugin;
import net.drapuria.framework.bukkit.impl.command.CommandMapImpl;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.impl.command.ICommandMap;
import net.drapuria.framework.bukkit.impl.command.parameter.type.CommandTypeParameter;
import net.drapuria.framework.bukkit.impl.command.parameter.type.CommandTypeParameterComponentHolder;
import net.drapuria.framework.bukkit.impl.command.repository.BukkitCommandRepository;
import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.DrapuriaInstanceFactory;
import net.drapuria.framework.annotations.NewInstance;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.CommandParameter;
import net.drapuria.framework.command.annotation.SubCommand;
import net.drapuria.framework.command.provider.CommandProvider;
import net.drapuria.framework.command.service.CommandService;
import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.plugin.PluginClassLoader;
import net.drapuria.framework.plugin.PluginListenerAdapter;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.beans.component.ComponentRegistry;
import net.drapuria.framework.util.Stacktrace;
import net.drapuria.framework.util.TypeAnnotationScanner;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BukkitCommandProvider extends CommandProvider<DrapuriaCommand, CommandTypeParameter<?>> {

    private final CommandService commandService;
    private ICommandMap drapuriaCommandMap;
    private Object oldCommandMap;

    public BukkitCommandProvider(CommandService commandService) {
        super(new BukkitCommandRepository(), DrapuriaCommand.class);
        this.commandService = commandService;
        this.commandService.setCommandAnnotation(Command.class);
        this.commandService.setSubCommandAnnotation(SubCommand.class);
        this.commandService.setParameterAnnotation(CommandParameter.class);

        PluginManager.INSTANCE.registerListener(new PluginListenerAdapter() {
            @Override
            public void onPluginPostEnable(AbstractPlugin plugin) {
                if (plugin.getClass().isAnnotationPresent(ClasspathScan.class)) {
                    for (String packageName : plugin.getClass().getAnnotation(ClasspathScan.class).value())
                        loadCommands(plugin, packageName);

                } else
                    loadCommands(plugin, "");
            }

            @Override
            public int priority() {
                return -5;
            }
        });
    }

    @Override
    public void registerDefaults() {
        ((BukkitCommandRepository) getCommandRepository()).setCommandProvider(this);
        registerCommandMap();
        ComponentRegistry.registerComponentHolder(new CommandTypeParameterComponentHolder(this));
    }

    @Override
    public void shutdown() {
        unregisterCommandMap();
    }

    public CommandTypeParameter<?> getTypeParameter(Class<?> type) {
        return this.getCommandTypeParameterParser().get(type);
    }

    @SneakyThrows
    private void unregisterCommandMap() {
        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        commandMapField.set(Bukkit.getServer(), oldCommandMap);
    }

    @SneakyThrows
    private void registerCommandMap() {
        TypeAnnotationScanner scanner = new TypeAnnotationScanner(CommandMapImpl.class);
        scanner.getResult().stream().findFirst().ifPresent(aClass -> {
            Object o = null;
            Field commandMapField = null;
            try {
                commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);

                this.drapuriaCommandMap = (ICommandMap) aClass.getDeclaredConstructor(Server.class, BukkitCommandProvider.class)
                        .newInstance(Bukkit.getServer(), BukkitCommandProvider.this);
                oldCommandMap = commandMapField.get(Bukkit.getServer());
                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                if (PluginClassLoader.isJava9OrNewer()) {
                    Class<?> fieldHelper = Class.forName("net.drapuria.framework.bukkit.java.FieldHelper");
                    fieldHelper.getMethod("makeNonFinal", Field.class).invoke(null, knownCommandsField);
                } else {
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(knownCommandsField, knownCommandsField.getModifiers() & ~Modifier.FINAL);
                }
                knownCommandsField.set(drapuriaCommandMap, knownCommandsField.get(oldCommandMap));
                commandMapField.set(Bukkit.getServer(), drapuriaCommandMap);
            } catch (ClassNotFoundException | NoSuchFieldException | InstantiationException
                    | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

    public Object getOldCommandMap() {
        return oldCommandMap;
    }

    public ICommandMap getDrapuriaCommandMap() {
        return drapuriaCommandMap;
    }

    public void loadCommands(Object source, String packageName) {
        List<Class<?>> commandTypeList = source == null ?
                findCommandClasses() :
                findCommandClasses(source.getClass().getProtectionDomain().getCodeSource(), packageName);
        commandTypeList.forEach(commandClass -> loadCommand(source, commandClass));
    }

    private void loadCommand(Object source, Class<?> commandClass) {
        try {
            final Object instance = constructorDetails(commandClass).newInstance(BeanContext.INSTANCE);
            registerCommand(source, new DrapuriaCommand(commandService, instance));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}