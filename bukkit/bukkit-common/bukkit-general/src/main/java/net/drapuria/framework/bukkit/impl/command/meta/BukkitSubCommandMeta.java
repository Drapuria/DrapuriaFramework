/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.meta;

import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.impl.command.PlayerParameter;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameter;
import net.drapuria.framework.bukkit.player.PlayerRepository;
import lombok.Getter;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameterData;
import net.drapuria.framework.bukkit.impl.command.parameter.type.CommandTypeParameter;
import net.drapuria.framework.command.annotation.SubCommand;
import net.drapuria.framework.command.meta.CommandMeta;
import net.drapuria.framework.command.meta.SubCommandMeta;
import net.drapuria.framework.command.parameter.Parameter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public class BukkitSubCommandMeta extends SubCommandMeta<Player, BukkitParameterData> {

    private final String commandPermission;
    private final DrapuriaCommand parent;
    private final SubCommand subCommand;
    private final boolean useDrapuriaPlayer;
    public BukkitSubCommandMeta(CommandMeta<Player, ?> commandMeta, SubCommand subCommand, BukkitParameterData parameterData, Object instance, Method method, boolean useDrapuriaPlayer, DrapuriaCommand parent) {
        super(commandMeta, parameterData, subCommand.names(), instance, method, subCommand.parameters());
        this.subCommand = subCommand;
        this.useDrapuriaPlayer = useDrapuriaPlayer;
        this.commandPermission = this.subCommand.permission();
        this.parent = parent;
    }

    @SuppressWarnings({"DuplicatedCode", "Convert2streamapi"})
    @Override
    public boolean execute(Player executor, String[] params) {
        Object[] objects = new Object[this.parameterData.getParameterCount() + 1];
        objects[0] = useDrapuriaPlayer ? PlayerRepository.getRepository.findById(executor.getUniqueId()).get() : executor;
        for (int i = 0; i < this.parameterData.getParameterCount(); i++) {
            Parameter parameter = this.parameterData.getParameters()[i];
            if (i == params.length) {
                if (this.commandMeta != null && commandMeta.getMethod() != null) {
                    commandMeta.execute(executor, params);
                }
                return true;
            }

            CommandTypeParameter<?> commandTypeParameter = Drapuria.getCommandProvider.getTypeParameter(parameter.getClassType());

            if (commandTypeParameter == null)
                throw new NullPointerException("Found no type parameter for class: " + parameter.getClassType());

            if (parameter.getClassType() == String.class && (i + 1) >= this.parameterData.getParameterCount() && (i + 1) < params.length) {
                String builder = Arrays.stream(params, i, params.length).collect(Collectors.joining(" "));
                if (parameter.isWildcard()) {
                    /*
                    StringBuilder stringBuilder = new StringBuilder(builder);
                    for (int index = i; index < params.length; index++) {
                        stringBuilder.append(" ").append(params[index]);
                    }
                     */
                    objects[i + 1] = builder;
                    break;
                } else
                    objects[i + 1] = builder.split(" ")[0];
            } else {
                BukkitParameter bukkitParameter = parameterData.get(i);
                if (bukkitParameter.getClassType() == Player.class && bukkitParameter.getJavaParameter().isAnnotationPresent(PlayerParameter.class)) {
                    if (bukkitParameter.getJavaParameter().getAnnotation(PlayerParameter.class).hasToBeOnline()) {
                        final Object object = commandTypeParameter.parse(executor,  params[i]);
                        if (object == null) {
                            parent.playerNotFound(PlayerRepository.getRepository.findById(executor.getUniqueId()).get(), params[i]);
                            return true;
                        }
                        objects[i + 1] = object;
                    }
                } else
                    objects[i + 1] = commandTypeParameter.parse(executor, params[i]);
               // objects[i + 1] = commandTypeParameter.parse(executor, params[i]);
            }
        }
        try {
            this.method.invoke(this.instance, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean canAccess(Player executor) {
        if ("".equalsIgnoreCase(this.commandPermission))
            return true;
        return executor.hasPermission(this.commandPermission);
    }
}
