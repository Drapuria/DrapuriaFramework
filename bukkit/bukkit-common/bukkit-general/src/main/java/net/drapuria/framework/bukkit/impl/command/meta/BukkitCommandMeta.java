/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.meta;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.impl.command.PlayerParameter;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameter;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameterData;
import net.drapuria.framework.bukkit.impl.command.parameter.type.CommandTypeParameter;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.bukkit.player.PlayerRepository;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.CommandParameter;
import net.drapuria.framework.command.annotation.Executor;
import net.drapuria.framework.command.annotation.NullParameterAction;
import net.drapuria.framework.command.annotation.SubCommand;
import net.drapuria.framework.command.meta.CommandMeta;
import net.drapuria.framework.command.parameter.Parameter;
import net.drapuria.framework.command.parameter.ParameterData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class BukkitCommandMeta extends CommandMeta<Player, BukkitParameterData> {

    private String commandPermission;

    private DrapuriaCommand parent;
    private final Map<String, List<String>> activeAliases; // das hier ist kein alias sondern subcommand alias?
    // private final Map<String, BukkitSubCommandMeta> subCommandMeta;
    private final Map<String, Set<BukkitSubCommandMeta>> subCommandMeta;
    private final Collection<BukkitSubCommandMeta> subCommandMetaCollection;
    private boolean useDrapuriaPlayer;
    final Map<Integer, Integer> methodsWithSameParameterCount = new HashMap<>();

    public BukkitCommandMeta(DrapuriaCommand parent) {
        super(parent.getInstance(), parent.getName(), null);
        this.parent = parent;
        this.activeAliases = new HashMap<>();

        this.fetchCommands();
        super.parameterDatas.sort(Comparator.comparingInt(ParameterData::getParameterCount));
        Collections.reverse(super.parameterDatas);
        // this.subCommandMeta = this.fetchSubCommands();
        this.subCommandMeta = this.fetchSubCommands();

        //   this.parent.getAliases().remove(this.commandName);
        this.parent.setName(this.commandName);
        this.parent.setDescription(this.commandDescription);
        //   this.commandAliases = Arrays.copyOfRange(this.commandAliases, 1, this.commandAliases.length);
        this.parent.setAliases(Arrays.asList(super.commandAliases));
        // this.parent.setPermission(this.commandPermission);
        System.out.println("registered COMMAND WITH NAME: " + this.commandName);
        System.out.println(Arrays.toString(this.commandAliases));
        System.out.println(this.activeAliases);
        this.subCommandMetaCollection = this.subCommandMeta.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        this.parameterDatas.forEach(bukkitParameterData ->
                methodsWithSameParameterCount.merge(bukkitParameterData.getParameterCount(), 1, Integer::sum));
    }

    public void setCommandPermission(String commandPermission) {
        this.commandPermission = commandPermission;
    }

    public List<BukkitSubCommandMeta> getValidSubCommandMetas(String input) {
        return this.activeAliases.entrySet()
                .parallelStream()
                .filter(entry -> entry.getValue().contains(input))
                .map(entry -> this.subCommandMeta.get(entry.getKey()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /*
    public BukkitSubCommandMeta getSubCommandMeta(String input) {
        return this.activeAliases.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(input))
                .findFirst()
                .map(entry -> this.subCommandMeta.get(entry.getKey()))
                .orElse(null);
    }
        */


    @SuppressWarnings("DuplicatedCode")
    private void fetchCommands() {
        super.parameterDatas = new ArrayList<>();
        Command command = this.parent.getInstance().getClass().getAnnotation(Command.class);
        if (command == null) {
            Drapuria.LOGGER.warn("COMMAND IS NULL");
            return;
        }

        this.commandPermission = command.permission();
        this.commandDescription = command.description();
        this.commandAliases = command.names();
        this.commandName = this.commandAliases[0];
        Method[] methods = this.parent.getInstance().getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Executor.class)) {
                Drapuria.LOGGER.info("Found command method " + method.getName());
                Executor executor = method.getAnnotation(Executor.class);

                Class<?>[] parameterTypes = method.getParameterTypes();
                System.out.println("para length: " + parameterTypes.length);
                System.out.println("para: " + Arrays.toString(parameterTypes));
                String[] annotationParameterTypes = executor.parameters();
                if (parameterTypes.length == 0)
                    break;
                if (parameterTypes[0] == DrapuriaPlayer.class) {
                    useDrapuriaPlayer = true;
                }
                BukkitParameter[] parameters = new BukkitParameter[parameterTypes.length - 1];
                for (int i = 1; i < parameterTypes.length; i++) {
                    Class<?> parameter = method.getParameterTypes()[i];
                    final NullParameterAction nullParameterAction = parameter.isAnnotationPresent(NullParameterAction.class)
                            ? parameter.getAnnotation(NullParameterAction.class) : null;
                    if (method.getParameters()[i].isAnnotationPresent(CommandParameter.class)) {
                        CommandParameter parameterInfo = method.getParameters()[i]
                                .getAnnotation(CommandParameter.class);
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                parameterInfo.defaultValue(),
                                parameterInfo.wildcard(),
                                parameterInfo.allowNull(),
                                parameterInfo.tabCompleteFlags(),
                                method.getParameters()[i],
                                nullParameterAction);

                    } else {
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                "",
                                false,
                                false,
                                new String[]{},
                                method.getParameters()[i],
                                nullParameterAction);
                    }
                }
                parameterDatas.add(new BukkitParameterData(method, parameters, executor.labels().length == 0 ? Sets.newHashSet() : Sets.newHashSet(executor.labels())));
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private Map<String, Set<BukkitSubCommandMeta>> fetchSubCommands() {
        Map<String, Set<BukkitSubCommandMeta>> tmpHashMap = new HashMap<>();
        Method[] methods = this.parent.getInstance().getClass().getDeclaredMethods();

        for (Method method : methods)
            if (method.isAnnotationPresent(SubCommand.class)) {
                SubCommand subCommand = method.getAnnotation(SubCommand.class);

                Class<?>[] parameterTypes = method.getParameterTypes();
                String[] annotationParameterTypes = StringUtils.substringsBetween(subCommand.parameters(), "{", "}");

                if (parameterTypes.length == 0) continue;
                BukkitParameter[] parameters = new BukkitParameter[parameterTypes.length - 1];
                for (int i = 1; i < parameterTypes.length; i++) {
                    Class<?> parameter = method.getParameterTypes()[i];
                    final NullParameterAction nullParameterAction = parameter.isAnnotationPresent(NullParameterAction.class) ? parameter.getAnnotation(NullParameterAction.class) : null;
                    if (method.getParameters()[i].isAnnotationPresent(CommandParameter.class)) {
                        CommandParameter parameterInfo = method.getParameters()[i].getAnnotation(CommandParameter.class);
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                parameterInfo.defaultValue(),
                                parameterInfo.wildcard(),
                                parameterInfo.allowNull(),
                                parameterInfo.tabCompleteFlags(),
                                method.getParameters()[i],
                                nullParameterAction);

                    } else {
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                "",
                                false,
                                false,
                                new String[]{},
                                method.getParameters()[i],
                                nullParameterAction);
                    }
                }
                BukkitParameterData parameterData = new BukkitParameterData(method, parameters, subCommand.labels().length == 0 ? Sets.newHashSet() : Sets.newHashSet(subCommand.labels()));
                BukkitSubCommandMeta meta = new BukkitSubCommandMeta(this, subCommand, parameterData, this.parent.getInstance(), method, parameterTypes[0] == DrapuriaPlayer.class, parent);

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
    public void execute(Player executor, String label, String[] params) {
        if (isAsyncDefaultCommand && Bukkit.isPrimaryThread()) {
            DrapuriaCommon.executorService.execute(() -> execute(executor, label, params));
            return;
        }
        System.out.println("params length: " + params.length);
        BukkitParameterData foundData = null;
        Object[] foundObjects = null;
        boolean allMatches = true;
        parameterLoop:
        for (BukkitParameterData parameterData : this.parameterDatas) {
            if (!parameterData.isValidLabel(label))
                continue;
            Object[] objects = new Object[parameterData.getParameterCount() + 1];
            objects[0] = useDrapuriaPlayer ? PlayerRepository.getRepository.findById(executor.getUniqueId()).get() : executor;
            final int totalMethodsWithCount = this.methodsWithSameParameterCount.get(parameterData.getParameterCount());
            for (int i = 0; i < parameterData.getParameterCount(); i++) {
                System.out.println("i: " + i);
                if (i == params.length) {
                    if (parameterData.getParameterCount() == i) break;
                    BukkitParameter bukkitParameter = parameterData.get(i);
                    CommandTypeParameter<?> commandTypeParameter = Drapuria.getCommandProvider.getTypeParameter(bukkitParameter.getClassType());
                    final Object object = commandTypeParameter.parse(executor, bukkitParameter.getDefaultValue());
                    objects[i + 1] = object;
                    if (object == null) {
                        System.out.println("CONTINUE");
                        continue parameterLoop;
                    }
                    if (allMatches) {
                        System.out.println("ALL MATCHES BREAKING");
                        foundData = parameterData;
                        foundObjects = objects;
                        break parameterLoop;
                    }
                    System.out.println("i == params.length CONTINUE");
                    continue parameterLoop;
                }
                Parameter parameter = parameterData.getParameters()[i];
                CommandTypeParameter<?> commandTypeParameter = Drapuria.getCommandProvider.getTypeParameter(parameter.getClassType());
                if (commandTypeParameter == null)
                    throw new NullPointerException("Found no type parameter for class: " + parameter.getClassType());

                if (parameter.getClassType() == String.class && i + 1 >= parameterData.getParameterCount() && i + 1 < params.length) {
                    String builder = Arrays.stream(params, i, params.length).collect(Collectors.joining(" "));
                    if (parameter.isWildcard()) {
                        StringBuilder stringBuilder = new StringBuilder(builder);
                        for (int index = i; index < params.length; index++) {
                            if (stringBuilder.length() > 0)
                                stringBuilder.append(" ");
                            stringBuilder.append(params[index]);
                        }
                        objects[i + 1] = stringBuilder.toString();
                    } else {
                        Object object = commandTypeParameter.parse(executor, params[i]);
                        if (object == null && totalMethodsWithCount > 1) {
                            allMatches = false;
                            continue parameterLoop;
                        }
                        objects[i + 1] = object;
                    }
                } else {
                    System.out.println("HALLO");
                    BukkitParameter bukkitParameter = parameterData.get(i);
                    if (bukkitParameter.getClassType() == Player.class && bukkitParameter.getJavaParameter().isAnnotationPresent(PlayerParameter.class)) {
                        if (bukkitParameter.getJavaParameter().getAnnotation(PlayerParameter.class).hasToBeOnline()) {
                            final Object object = commandTypeParameter.parse(executor, params[i]);
                            if (object == null) {
                                parent.playerNotFound(PlayerRepository.getRepository.findById(executor.getUniqueId()).get(), params[i]);
                                return;
                            }
                            // objects[i + 1] = object;
                            System.out.println("PLAYER typeparameter type " + commandTypeParameter.getType());
                            System.out.println("object " + object);
                            System.out.println("params[i] " + params[i]);
                            System.out.println("i: " + i);
                            System.out.println("param count: " + parameterData.getParameterCount());
                            if (object == null && totalMethodsWithCount > 1) {
                                allMatches = false;
                                System.out.println("x2");
                                continue parameterLoop;
                            }
                            System.out.println("...");
                            objects[i + 1] = object;
                            if (i + 1 == parameterData.getParameterCount()) {
                                allMatches = true;
                                foundData = parameterData;
                                foundObjects = objects;
                                System.out.println("breaking?");
                                break parameterLoop;
                            }
                        }
                    } else {
                        Object object = commandTypeParameter.parse(executor, params[i]);
                        System.out.println("typeparameter type " + commandTypeParameter.getType());
                        System.out.println("object " + object);
                        System.out.println("params[i] " + params[i]);
                        System.out.println("i: " + i);
                        System.out.println("param count: " + parameterData.getParameterCount());
                        if (object == null && totalMethodsWithCount > 1) {
                            allMatches = false;
                            System.out.println("x2");
                            continue parameterLoop;
                        }
                        System.out.println("...");
                        objects[i + 1] = object;
                        if (i + 1 == parameterData.getParameterCount()) {
                            allMatches = true;
                            foundData = parameterData;
                            foundObjects = objects;
                            System.out.println("breaking?");
                            break parameterLoop;
                        }
                    }
                }
            }
            if (parameterData.getParameterCount() == 0 && params.length == 0) {
                foundData = parameterData;
                foundObjects = objects;
                break;
            }
        }
        if (foundData == null) {
            executor.sendMessage(parent.generateDefaultUsage(null, label));
            System.out.println("foundData == null");
            return;
        }
        System.out.println("parameter count: " + foundData.getParameterCount());
        System.out.println("objects length: " + foundObjects.length);
        System.out.println("parameters: " + Arrays.toString(foundData.getParameters()));
        System.out.println("objects: " + Arrays.toString(foundObjects));
        if (foundObjects.length - 1 != foundData.getParameterCount()) // - 1 since we have the player parameter?
            return;
        try {
            foundData.getMethod().invoke(this.instance, foundObjects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return; // TODO: RETURN?
    }

    @Override
    public List<BukkitParameterData> getParameterDatas() {
        return super.getParameterDatas();
    }
}