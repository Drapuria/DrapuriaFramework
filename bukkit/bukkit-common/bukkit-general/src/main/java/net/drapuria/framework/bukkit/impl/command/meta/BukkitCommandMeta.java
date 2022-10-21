/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.meta;

import net.drapuria.framework.bukkit.impl.command.PlayerParameter;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.bukkit.player.PlayerRepository;
import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameter;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameterData;
import net.drapuria.framework.bukkit.impl.command.parameter.type.CommandTypeParameter;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.DefaultCommand;
import net.drapuria.framework.command.annotation.CommandParameter;
import net.drapuria.framework.command.annotation.SubCommand;
import net.drapuria.framework.command.meta.CommandMeta;
import net.drapuria.framework.command.parameter.Parameter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class BukkitCommandMeta extends CommandMeta<Player, BukkitParameterData> {

    private String commandPermission;

    private DrapuriaCommand parent;
    private final Map<String, List<String>> activeAliases; // das hier ist kein alias sondern subcommand alias?
    private final Map<String, BukkitSubCommandMeta> subCommandMeta;
    private final Map<String, Set<BukkitSubCommandMeta>> subCommandMeta2;
    private boolean isUseUnlySubCommands;
    private boolean useDrapuriaPlayer;

    public BukkitCommandMeta(DrapuriaCommand parent) {
        super(parent, parent.getName(), null);
        this.parent = parent;
        this.activeAliases = new HashMap<>();

        this.fetchCommands();
        this.subCommandMeta = this.fetchSubCommands();
        this.subCommandMeta2 = this.fetchSubCommands2();

     //   this.parent.getAliases().remove(this.commandName);
        this.parent.setName(this.commandName);
        this.parent.setDescription(this.commandDescription);
     //   this.commandAliases = Arrays.copyOfRange(this.commandAliases, 1, this.commandAliases.length);
        this.parent.setAliases(Arrays.asList(this.commandAliases));
        // this.parent.setPermission(this.commandPermission);
        System.out.println("registered COMMAND WITH NAME: " + this.commandName);
        System.out.println(Arrays.toString(this.commandAliases));
        System.out.println(this.activeAliases);
    }

    public void setCommandPermission(String commandPermission) {
        this.commandPermission = commandPermission;
    }

    public List<BukkitSubCommandMeta> getValidSubCommandMetas(String input) {
        return this.activeAliases.entrySet()
                .parallelStream()
                .filter(entry -> entry.getValue().contains(input))
                .map(entry -> this.subCommandMeta2.get(entry.getKey()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public BukkitSubCommandMeta getSubCommandMeta(String input) {
        /*
        System.out.println("META: " + meta.stream()
                .flatMap(x -> Arrays.stream(x.getParameterData().getParameters()))
                .map(Parameter::getParameter)
                .collect(Collectors.joining(",")));
         */
        return this.activeAliases.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(input))
                .findFirst()
                .map(entry -> this.subCommandMeta.get(entry.getKey()))
                .orElse(null);
    }


    @SuppressWarnings("DuplicatedCode")
    private void fetchCommands() {
        Command command = this.parent.getClass().getAnnotation(Command.class);

        if (command == null) return;

        this.commandPermission = command.permission();
        this.commandDescription = command.description();
        this.commandAliases = command.names();
        this.isUseUnlySubCommands = command.useSubCommandsOnly();
        this.commandName = this.commandAliases[0];
        if (!command.useSubCommandsOnly()) {
            Method[] methods = this.parent.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(DefaultCommand.class)) {
                    DefaultCommand executor = method.getAnnotation(DefaultCommand.class);

                    Class<?>[] parameterTypes = method.getParameterTypes();
                    String[] annotationParameterTypes = StringUtils.substringsBetween(executor.parameters(),
                            "{", "}");
                    if (parameterTypes.length == 0)
                        break;
                    if (parameterTypes[0] == DrapuriaPlayer.class) {
                        useDrapuriaPlayer = true;
                    }
                    BukkitParameter[] parameters = new BukkitParameter[parameterTypes.length - 1];
                    for (int i = 1; i < parameterTypes.length; i++) {
                        Class<?> parameter = method.getParameterTypes()[i];
                        if (method.getParameters()[i].isAnnotationPresent(CommandParameter.class)) {
                            CommandParameter parameterInfo = method.getParameters()[i]
                                    .getAnnotation(CommandParameter.class);
                            parameters[i - 1] = new BukkitParameter(parameter,
                                    annotationParameterTypes[i - 1],
                                    parameterInfo.defaultValue(),
                                    parameterInfo.wildcard(),
                                    parameterInfo.tabCompleteFlags(),
                                    method.getParameters()[i]);

                        } else {
                            parameters[i - 1] = new BukkitParameter(parameter,
                                    annotationParameterTypes[i - 1],
                                    "",
                                    false,
                                    new String[]{},
                                    method.getParameters()[i]);
                        }
                    }
                    this.parameterData = new BukkitParameterData(parameters);
                    this.method = method;
                    return;
                }
            }
        }
    }


    @SuppressWarnings("DuplicatedCode")
    private Map<String, BukkitSubCommandMeta> fetchSubCommands() {
        Map<String, BukkitSubCommandMeta> tmpHashMap = new HashMap<>();
        Method[] methods = this.parent.getClass().getDeclaredMethods();

        for (Method method : methods)
            if (method.isAnnotationPresent(SubCommand.class)) {
                SubCommand subCommand = method.getAnnotation(SubCommand.class);

                Class<?>[] parameterTypes = method.getParameterTypes();
                String[] annotationParameterTypes = StringUtils.substringsBetween(subCommand.parameters(), "{", "}");

                if (parameterTypes.length == 0) continue;
                BukkitParameter[] parameters = new BukkitParameter[parameterTypes.length - 1];
                for (int i = 1; i < parameterTypes.length; i++) {
                    Class<?> parameter = method.getParameterTypes()[i];
                    if (method.getParameters()[i].isAnnotationPresent(CommandParameter.class)) {
                        CommandParameter parameterInfo = method.getParameters()[i].getAnnotation(CommandParameter.class);
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                parameterInfo.defaultValue(),
                                parameterInfo.wildcard(),
                                parameterInfo.tabCompleteFlags(),
                                method.getParameters()[i]);

                    } else {
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                "",
                                false,
                                new String[]{},
                                method.getParameters()[i]);
                    }
                }
                BukkitParameterData parameterData = new BukkitParameterData(parameters);
                BukkitSubCommandMeta meta = new BukkitSubCommandMeta(this, subCommand, parameterData, this.parent, method, parameterTypes[0] == DrapuriaPlayer.class, parent);

                String defaultAlias = meta.getDefaultAlias();
                System.out.println("defaultAlias: " + defaultAlias);
                tmpHashMap.put(defaultAlias, meta);
             //   this.activeAliases.put(defaultAlias, Arrays.asList(meta.getAliases()));
            }
        return tmpHashMap;
    }

    @SuppressWarnings("DuplicatedCode")
    private Map<String, Set<BukkitSubCommandMeta>> fetchSubCommands2() {
        Map<String, Set<BukkitSubCommandMeta>> tmpHashMap = new HashMap<>();
        Method[] methods = this.parent.getClass().getDeclaredMethods();

        for (Method method : methods)
            if (method.isAnnotationPresent(SubCommand.class)) {
                SubCommand subCommand = method.getAnnotation(SubCommand.class);

                Class<?>[] parameterTypes = method.getParameterTypes();
                String[] annotationParameterTypes = StringUtils.substringsBetween(subCommand.parameters(), "{", "}");

                if (parameterTypes.length == 0) continue;
                BukkitParameter[] parameters = new BukkitParameter[parameterTypes.length - 1];
                for (int i = 1; i < parameterTypes.length; i++) {
                    Class<?> parameter = method.getParameterTypes()[i];
                    if (method.getParameters()[i].isAnnotationPresent(CommandParameter.class)) {
                        CommandParameter parameterInfo = method.getParameters()[i].getAnnotation(CommandParameter.class);
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                parameterInfo.defaultValue(),
                                parameterInfo.wildcard(),
                                parameterInfo.tabCompleteFlags(),
                                method.getParameters()[i]);

                    } else {
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                "",
                                false,
                                new String[]{},
                                method.getParameters()[i]);
                    }
                }
                BukkitParameterData parameterData = new BukkitParameterData(parameters);
                BukkitSubCommandMeta meta = new BukkitSubCommandMeta(this, subCommand, parameterData, this.parent, method, parameterTypes[0] == DrapuriaPlayer.class, parent);

                String defaultAlias = meta.getDefaultAlias();
                //    System.out.println("defaultAlias: " + defaultAlias);
                if (!tmpHashMap.containsKey(defaultAlias)) {
                    tmpHashMap.put(defaultAlias, new HashSet<>());
                }
                tmpHashMap.get(defaultAlias).add(meta);
                this.activeAliases.put(defaultAlias, Arrays.asList(meta.getAliases()));
            }
        return tmpHashMap;
    }

    @Override
    public boolean canAccess(Player executor) {
        if ("".equalsIgnoreCase(this.commandPermission))
            return true;
        return executor.hasPermission(this.commandPermission);
    }

    @Override
    @SuppressWarnings({"DuplicatedCode", "Convert2streamapi"})
    public void execute(Player executor, String[] params) {
        if (isAsyncDefaultCommand && Bukkit.isPrimaryThread()) {
            DrapuriaCommon.executorService.execute(() -> execute(executor, params));
            return;
        }
        Object[] objects = new Object[this.parameterData.getParameterCount() + 1];
        objects[0] = useDrapuriaPlayer ? PlayerRepository.getRepository.findById(executor.getUniqueId()).get() : executor;

        for (int i = 0; i < this.parameterData.getParameterCount(); i++) {
            if (i == params.length) {
                if (parameterData.getParameterCount() == i) break;
                BukkitParameter bukkitParameter = parameterData.get(i);
                CommandTypeParameter<?> commandTypeParameter = Drapuria.getCommandProvider.getTypeParameter(bukkitParameter.getClassType());
                objects[i + 1] = commandTypeParameter.parse(executor, bukkitParameter.getDefaultValue());
                break;
            }
            Parameter parameter = this.parameterData.getParameters()[i];
            CommandTypeParameter<?> commandTypeParameter = Drapuria.getCommandProvider.getTypeParameter(parameter.getClassType());
            if (commandTypeParameter == null)
                throw new NullPointerException("Found no type parameter for class: " + parameter.getClassType());

            if (parameter.getClassType() == String.class && i + 1 >= this.parameterData.getParameterCount() && i + 1 < params.length) {
                String builder = Arrays.stream(params, i, params.length).collect(Collectors.joining(" "));
                if (parameter.isWildcard()) {
                    StringBuilder stringBuilder = new StringBuilder(builder);
                    for (int index = i; index < params.length; index++) {
                        if (stringBuilder.length() > 0)
                            stringBuilder.append(" ");
                        stringBuilder.append(params[index]);
                    }
                    objects[i + 1] = stringBuilder.toString();
                } else
                    objects[i + 1] = builder;
            } else {
                BukkitParameter bukkitParameter = parameterData.get(i);
                if (bukkitParameter.getClassType() == Player.class && bukkitParameter.getJavaParameter().isAnnotationPresent(PlayerParameter.class)) {
                    if (bukkitParameter.getJavaParameter().getAnnotation(PlayerParameter.class).hasToBeOnline()) {
                        final Object object = commandTypeParameter.parse(executor, params[i]);
                        if (object == null) {
                            parent.playerNotFound(PlayerRepository.getRepository.findById(executor.getUniqueId()).get(), params[i]);
                            return;
                        }
                        objects[i + 1] = object;
                    }
                } else
                    objects[i + 1] = commandTypeParameter.parse(executor, params[i]);
            }
        }
        try {
            this.method.invoke(this.instance, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}