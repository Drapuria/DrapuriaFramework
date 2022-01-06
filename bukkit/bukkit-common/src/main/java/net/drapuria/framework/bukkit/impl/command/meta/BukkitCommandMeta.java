package net.drapuria.framework.bukkit.impl.command.meta;

import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameter;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameterData;
import net.drapuria.framework.bukkit.impl.command.parameter.type.CommandTypeParameter;
import net.drapuria.framework.command.annotations.Command;
import net.drapuria.framework.command.annotations.DefaultCommand;
import net.drapuria.framework.command.annotations.CommandParameter;
import net.drapuria.framework.command.annotations.SubCommand;
import net.drapuria.framework.command.meta.CommandMeta;
import net.drapuria.framework.command.parameter.Parameter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class BukkitCommandMeta extends CommandMeta<Player, BukkitParameterData> {

    private String commandPermission;

    private DrapuriaCommand parent;
    private final Map<String, List<String>> activeAliases;
    private final Map<String, BukkitSubCommandMeta> subCommandMeta;
    private boolean isUseUnlySubCommands;

    public BukkitCommandMeta(DrapuriaCommand parent) {
        super(parent, parent.getName(), null);
        this.parent = parent;
        this.activeAliases = new HashMap<>();

        this.fetchCommands();
        this.subCommandMeta = this.fetchSubCommands();
        this.parent.setAliases(Arrays.asList(this.commandAliases));
        for (String commandAlias : commandAliases) {
            System.out.println(commandAlias);
        }
        this.parent.setDescription(this.commandDescription);
        // this.parent.setPermission(this.commandPermission);
    }

    public void setCommandPermission(String commandPermission) {
        this.commandPermission = commandPermission;
    }

    public BukkitSubCommandMeta getSubCommandMeta(String input) {
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
                                    parameterInfo.tabCompleteFlags());

                        } else {
                            parameters[i - 1] = new BukkitParameter(parameter,
                                    annotationParameterTypes[i - 1],
                                    "",
                                    false,
                                    new String[]{});
                        }
                    }
                    this.parameterData = new BukkitParameterData(parameters);
                    this.method = method;
                    return;
                }
            }
        }
    }

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
                                parameterInfo.tabCompleteFlags());

                    } else {
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                "",
                                false,
                                new String[]{});
                    }
                }

                BukkitParameterData parameterData = new BukkitParameterData(parameters);
                BukkitSubCommandMeta meta = new BukkitSubCommandMeta(subCommand, parameterData, this.parent, method);

                String defaultAlias = meta.getDefaultAlias();

                tmpHashMap.put(defaultAlias, meta);
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
    @SuppressWarnings("DuplicatedCode")
    public void execute(Player executor, String[] params) {
        if (isAsyncDefaultCommand && Bukkit.isPrimaryThread()) {
            DrapuriaCommon.executorService.execute(() -> execute(executor, params));
            return;
        }
        Object[] objects = new Object[this.parameterData.getParameterCount() + 1];
        objects[0] = executor;

        for (int i = 0; i < this.parameterData.getParameterCount(); i++) {
            if (i == params.length) return;
            Parameter parameter = this.parameterData.getParameters()[i];
            CommandTypeParameter<?> commandTypeParameter = Drapuria.getCommandProvider.getTypeParameter(parameter.getClassType());
            if (commandTypeParameter == null)
                throw new NullPointerException("Found no type parameter for class: " + parameter.getClassType());

            if (parameter.getClassType() == String.class && i + 1 >= this.parameterData.getParameterCount() && i + 1 < params.length) {
                String builder = Arrays.stream(params, i, params.length).collect(Collectors.joining(" "));
                if (parameter.isWildcard()) {
                    builder = IntStream.range(i, params.length).mapToObj(index -> " " + params[index]).collect(Collectors.joining("", builder, ""));
                    objects[i + 1] = builder;
                } else
                    objects[i + 1] = builder;
            } else {
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
