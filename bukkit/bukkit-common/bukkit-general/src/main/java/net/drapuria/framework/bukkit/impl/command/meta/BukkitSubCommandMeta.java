/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.meta;

import lombok.Getter;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.impl.command.context.BukkitCommandContext;
import net.drapuria.framework.bukkit.impl.command.executor.BukkitExecutorData;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameter;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameterData;
import net.drapuria.framework.bukkit.impl.command.parameter.type.CommandTypeParameter;
import net.drapuria.framework.bukkit.player.PlayerRepository;
import net.drapuria.framework.command.annotation.SubCommand;
import net.drapuria.framework.command.context.ParsedArgument;
import net.drapuria.framework.command.context.permission.PermissionContext;
import net.drapuria.framework.command.meta.CommandMeta;
import net.drapuria.framework.command.meta.SubCommandMeta;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public class BukkitSubCommandMeta extends SubCommandMeta<CommandSender, BukkitParameter, BukkitExecutorData, BukkitParameterData> {

    private final String commandPermission;
    private final DrapuriaCommand parent;
    private final SubCommand subCommand;
    private final boolean useDrapuriaPlayer;

    public BukkitSubCommandMeta(CommandMeta<CommandSender, BukkitParameter, BukkitExecutorData> commandMeta, SubCommand subCommand, BukkitParameterData parameterData, Object instance, Method method, boolean useDrapuriaPlayer,
                                DrapuriaCommand parent, PermissionContext<CommandSender> permissionContext, Set<String> labels) {
        super(commandMeta, new BukkitExecutorData(method, permissionContext, subCommand.permission(), new HashSet<>(Arrays.asList(subCommand.names())), parameterData, subCommand.names()[0]), labels);
        this.subCommand = subCommand;
        this.useDrapuriaPlayer = useDrapuriaPlayer;
        this.commandPermission = this.subCommand.permission();
        this.parent = parent;
    }

    public ParsedArgument<?>[] getParsedArguments(final CommandSender executor, final String[] params) {
        final boolean isPlayer = executor instanceof Player;
        final Player bukkitPlayer = isPlayer ? (Player) executor : null;
        final ParsedArgument<?>[] parsedArguments = new ParsedArgument[super.getExecutorData().getParameterData().getParameterCount()];
        for (int i = 0; i < super.executorData.getParameterData().getParameterCount(); i++) {
            final BukkitParameter parameter = super.getExecutorData().getParameterData().get(i);
            final String current = params.length == i ? parameter.getDefaultValue().isEmpty() ? null : parameter.getDefaultValue() : params[i];
            if (current == null)
                return parsedArguments;
            final CommandTypeParameter<?> typeParameter = Drapuria.getCommandProvider.getTypeParameter(parameter.getClassType());
            if (typeParameter == null)
                return parsedArguments;
            final Object parsedObject;
            if (isPlayer) {
                parsedObject = typeParameter.parse(bukkitPlayer, current);
            } else {
                parsedObject = typeParameter.parseNonPlayer(executor, current);
            }
            if (parsedObject == null)
                return parsedArguments;
            parsedArguments[i] = new ParsedArgument<>(i, parsedObject);
        }
        return parsedArguments;
    }

    public String isValidAlias(String input) {
        for (String alias : executorData.getAliases()) {
            if (input.startsWith(alias) || alias.startsWith(input))
                return alias;
        }
        return null;
    }

    @Override
    public boolean canAccess(CommandSender executor) {
        return executorData.canAccess(executor);
    }

    public void execute(BukkitCommandContext<? extends CommandSender> context, ParsedArgument<?>[] value) {
        Object[] methodArgs = new Object[value.length + 1];
        methodArgs[0] = useDrapuriaPlayer && context.getSource() instanceof Player ? PlayerRepository.getRepository.findById(((Player) context.getSource()).getUniqueId()).get() : context.getSource();
        System.arraycopy(Arrays.stream(value).map(parsedArgument -> (Object) parsedArgument.getResult()).toArray(), 0, methodArgs, 1, value.length);
        try {
            executorData.getMethod().invoke(parent.getInstance(), methodArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}