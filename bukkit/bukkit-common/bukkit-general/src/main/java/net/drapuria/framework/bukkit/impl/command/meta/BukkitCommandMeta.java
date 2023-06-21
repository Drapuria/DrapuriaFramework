/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.meta;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.impl.command.context.BukkitCommandContext;
import net.drapuria.framework.bukkit.impl.command.executor.BukkitExecutorData;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameter;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameterData;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.bukkit.player.PlayerRepository;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.CommandParameter;
import net.drapuria.framework.command.annotation.Executor;
import net.drapuria.framework.command.annotation.NullParameterAction;
import net.drapuria.framework.command.annotation.SubCommand;
import net.drapuria.framework.command.context.ParsedArgument;
import net.drapuria.framework.command.meta.CommandMeta;
import net.drapuria.framework.command.service.CommandService;
import net.drapuria.framework.util.entry.Entry;
import org.bukkit.command.CommandSender;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "DuplicatedCode"})
@Getter
public class BukkitCommandMeta extends CommandMeta<CommandSender, BukkitParameter, BukkitExecutorData> {

    // TODO REWRITE?
    // TODO REWRITE?
    // TODO REWRITE?
    // TODO REWRITE?

    /*
    WAS AUF JEDEN FALL GEMACHT WERDEN MUSS:
    CLEANUP
    CONSOLECOMMAND COMMANDS AUCH MÖGLICH MACHEN
     */

    private final CommandService commandService;
    private DrapuriaCommand parent;
    private String commandPermission;
    private final Map<String, List<String>> activeAliases; // das hier ist kein alias sondern subcommand alias?
    // private final Map<String, BukkitSubCommandMeta> subCommandMeta;
    private final Map<String, Set<BukkitSubCommandMeta>> subCommandMeta;
    private final Collection<BukkitSubCommandMeta> subCommandMetaCollection;
    private boolean useDrapuriaPlayer;
    final Map<Integer, Integer> methodsWithSameParameterCount = new HashMap<>();

    public BukkitCommandMeta(CommandService commandService, DrapuriaCommand parent) {
        super(parent.getInstance(), parent.getName(), null, null);
        this.commandService = commandService;
        this.parent = parent;
        this.activeAliases = new HashMap<>();

        this.fetchCommands();
        super.executorData.sort(Comparator.comparingInt(value -> value.getParameterData().getParameterCount()));
        // this.subCommandMeta = this.fetchSubCommands();
        this.subCommandMeta = this.fetchSubCommands();

        //   this.parent.getAliases().remove(this.commandName);
        this.parent.setName(this.commandName);
        this.parent.setDescription(this.commandDescription);
        //   this.commandAliases = Arrays.copyOfRange(this.commandAliases, 1, this.commandAliases.length);
        this.parent.setAliases(Arrays.asList(super.commandAliases));
        // this.parent.setPermission(this.commandPermission);
        // sort the executors by the needed parameter count
        this.executorData.sort(Comparator.comparingInt(value -> value.getParameterData().getParameterCount()));
        this.subCommandMetaCollection = this.subCommandMeta.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        this.executorData.forEach(executorData ->
                methodsWithSameParameterCount.merge(executorData.getParameterData().getParameterCount(), 1, Integer::sum));
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
        super.executorData = new ArrayList<>();
        Command command = this.parent.getInstance().getClass().getAnnotation(Command.class);
        if (command == null) {
            Drapuria.LOGGER.warn("COMMAND IS NULL");
            return;
        }
        this.commandService.getCommandProvider().findPermissionContext(command.permissionContext())
                .ifPresent(context -> super.permissionContext = context);
        this.commandPermission = command.permission();
        this.commandDescription = command.description();
        this.commandAliases = command.names();
        this.commandName = this.commandAliases[0];
        Method[] methods = this.parent.getInstance().getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Executor.class)) {
                Executor executor = method.getAnnotation(Executor.class);

                Class<?>[] parameterTypes = method.getParameterTypes();

                String[] annotationParameterTypes = executor.parameters();
                if (parameterTypes.length == 0)
                    break;
                if (parameterTypes[0] == DrapuriaPlayer.class) {
                    useDrapuriaPlayer = true;
                }
                BukkitParameter[] parameters = new BukkitParameter[parameterTypes.length - 1];
                for (int i = 1; i < parameterTypes.length; i++) {
                    Class<?> parameter = method.getParameterTypes()[i];
                    final NullParameterAction nullParameterAction = method.getParameters()[i].isAnnotationPresent(NullParameterAction.class)
                            ? method.getParameters()[i].getAnnotation(NullParameterAction.class) : null;
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
                                parent.getInstance().getClass(), parent.getInstance(), nullParameterAction);

                    } else {
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                "",
                                false,
                                false,
                                new String[]{},
                                method.getParameters()[i],
                                parent.getInstance().getClass(), parent.getInstance(), nullParameterAction);
                    }
                }
                executorData.add(
                        new BukkitExecutorData(
                                method,
                                Drapuria.getCommandProvider.findPermissionContext(executor.permissionContext()).orElse(null),
                                executor.permission(),
                                executor.labels().length == 0 ? Sets.newHashSet() : Sets.newHashSet(executor.labels()),
                                new BukkitParameterData(parameters), executor.labels().length == 0 ? this.commandName : executor.labels()[0]));
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
                String[] annotationParameterTypes = subCommand.parameters();

                if (parameterTypes.length == 0) continue;
                BukkitParameter[] parameters = new BukkitParameter[parameterTypes.length - 1];
                for (int i = 1; i < parameterTypes.length; i++) {
                    Class<?> parameter = method.getParameterTypes()[i];
                    final NullParameterAction nullParameterAction = method.getParameters()[i].isAnnotationPresent(NullParameterAction.class) ? method.getParameters()[i].getAnnotation(NullParameterAction.class) : null;
                    if (method.getParameters()[i].isAnnotationPresent(CommandParameter.class)) {
                        CommandParameter parameterInfo = method.getParameters()[i].getAnnotation(CommandParameter.class);
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                parameterInfo.defaultValue(),
                                parameterInfo.wildcard(),
                                parameterInfo.allowNull(),
                                parameterInfo.tabCompleteFlags(),
                                method.getParameters()[i],
                                parent.getInstance().getClass(), parent.getInstance(), nullParameterAction);

                    } else {
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                "",
                                false,
                                false,
                                new String[]{},
                                method.getParameters()[i],
                                parent.getInstance().getClass(), parent.getInstance(), nullParameterAction);
                    }
                }
                BukkitParameterData parameterData = new BukkitParameterData(parameters);
                BukkitSubCommandMeta meta = new BukkitSubCommandMeta(this, subCommand, parameterData, this.parent.getInstance(), method,
                        parameterTypes[0] == DrapuriaPlayer.class, parent, CommandService.INSTANCE.getCommandProvider().findPermissionContext(subCommand.permissionContext()).orElse(null), Sets.newHashSet(subCommand.labels()));

                String defaultAlias = meta.getExecutorData().getName();
                //    System.out.println("defaultAlias: " + defaultAlias);
                if (!tmpHashMap.containsKey(defaultAlias)) {
                    tmpHashMap.put(defaultAlias, new HashSet<>());
                }
                tmpHashMap.get(defaultAlias).add(meta);
                this.activeAliases.put(defaultAlias, meta.getExecutorData().getAliases());
            }
        return tmpHashMap;
    }

    @Override
    public boolean canAccess(CommandSender executor) {
        return super.getPermissionContext().hasPermission(executor, this.commandPermission);
    }

    @Override
    public void execute(CommandSender executor, String label, String[] params) {

    }


    public void execute2(BukkitCommandContext<? extends CommandSender> context) {
        final Optional<Entry<BukkitSubCommandMeta, ParsedArgument<?>[]>> optionalSubCommandEntry = this.findMatchingSubCommand(context);
        Entry<BukkitSubCommandMeta, ParsedArgument<?>[]> closestSubCommand = null;
        ParsedArgument<?> subCommandNullArgument = null;
        ParsedArgument<?> executorNullArgument = null;

        if (optionalSubCommandEntry.isPresent()) {
            closestSubCommand = optionalSubCommandEntry.get();
            subCommandNullArgument = containsNullObjects(closestSubCommand.getValue());
            if (subCommandNullArgument == null) {
                closestSubCommand.getKey().execute(context, closestSubCommand.getValue());
                return;
            } else {
                if (closestSubCommand.getKey().getExecutorData().getParameterData().get(subCommandNullArgument.getPosition()).handleNullParameterAction(context.getSource()))
                    return;
            }
        }

        final Optional<Entry<BukkitExecutorData, ParsedArgument<?>[]>> optionalExecutorDataEntry = this.findMatchingExecutor(context);
        Entry<BukkitExecutorData, ParsedArgument<?>[]> closestExecutor = null;
        if (optionalExecutorDataEntry.isPresent()) {
            closestExecutor = optionalExecutorDataEntry.get();
            executorNullArgument = containsNullObjects(closestExecutor.getValue());
            if (executorNullArgument == null) {
                this.execute(closestExecutor.getKey(), context, closestExecutor.getValue());
                return;
            }
        }
        if (!(context.getSource() instanceof Player)) {
            context.getSource().sendMessage("§cCould not execute this command via the console!");
            return;
        }
        if (closestExecutor != null && closestExecutor.getKey().getParameterData().get(executorNullArgument.getPosition()).handleNullParameterAction(context.getSource()))
            return;
        //                 parent.generateDefaultUsage(closestSubCommand.getKey(), context.getLabel());
        if (this.executorData.isEmpty() || this.executorData.get(0).getParameterData().getParameterCount() > 0) {
            showMessage(context.getSource(), "§cThis command has not been setup properly");
            return;
        }
        execute(this.executorData.get(0), context, new ParsedArgument<?>[0]);
    }

    public void execute(BukkitExecutorData executorData, BukkitCommandContext<? extends CommandSender> context, ParsedArgument<?>[] arguments) {
        Object[] methodArgs = new Object[arguments.length + 1];
        methodArgs[0] = useDrapuriaPlayer && context.getSource() instanceof Player ? PlayerRepository.getRepository.findById(((Player) context.getSource()).getUniqueId()).get() : context.getSource();
        System.arraycopy(Arrays.stream(arguments).map(parsedArgument -> (Object) parsedArgument.getResult()).toArray(), 0, methodArgs, 1, arguments.length);
        try {
            executorData.getMethod().invoke(parent.getInstance(), methodArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Entry<BukkitExecutorData, ParsedArgument<?>[]>> findMatchingExecutor(BukkitCommandContext<? extends CommandSender> context) {
        final String[] contextArguments = context.getUnparsedArguments();
        final String label = context.getLabel();
        final int insertedArgumentLength = contextArguments.length;
        Entry<BukkitExecutorData, ParsedArgument<?>[]> closestExecutor = null;

        for (BukkitExecutorData executorData : this.executorData) {
            if (!executorData.isValidLabel(label) ||!executorData.canAccess(context.getSource()))
                continue;
            final ParsedArgument<?>[] parsedArguments = executorData.getParsedArguments(context.getSource(), contextArguments);
            if (closestExecutor != null && isMoreOrEqualNullObjectsInSecondArray(closestExecutor.getValue(), parsedArguments, insertedArgumentLength)) {
                continue;
            }
            closestExecutor = new Entry<>(executorData, parsedArguments);
            if ((parsedArguments.length > 0 && parsedArguments[parsedArguments.length - 1] == null || contextArguments.length > parsedArguments.length))
                continue;
            System.out.println("RETURNING");
            return Optional.of(closestExecutor);
        }
        return Optional.ofNullable(closestExecutor);
    }

    private Optional<Entry<BukkitSubCommandMeta, ParsedArgument<?>[]>> findMatchingSubCommand(BukkitCommandContext<? extends CommandSender> context) {
        if (context.getInputLength() == 0) {
            return Optional.empty();
        }
        final String[] contextArguments = context.getUnparsedArguments();
        String[] subCommandArguments;
        int subCommandArgumentLength;
        Entry<BukkitSubCommandMeta, ParsedArgument<?>[]> closestSubCommand = null;
        for (BukkitSubCommandMeta bukkitSubCommandMeta : this.subCommandMetaCollection) {
            final String validAlias = bukkitSubCommandMeta.isValidAlias(context.getInput());
            if (bukkitSubCommandMeta.isValidLabel(context.getLabel()) && !bukkitSubCommandMeta.canAccess(context.getSource()) || validAlias == null)
                continue;
            subCommandArguments = Arrays.copyOfRange(contextArguments, validAlias.split(" ").length, contextArguments.length);
            subCommandArgumentLength = subCommandArguments.length;
            /*
            if (bukkitSubCommandMeta.getParameterData().getParameterCount() > subCommandArgumentLength)
                continue;
             */
            final ParsedArgument<?>[] parsedArguments = bukkitSubCommandMeta.getParsedArguments(context.getSource(), subCommandArguments);
            if (closestSubCommand != null && isMoreOrEqualNullObjectsInSecondArray(closestSubCommand.getValue(), parsedArguments, subCommandArgumentLength)) {
                continue;
            }
            closestSubCommand = new Entry<>(bukkitSubCommandMeta, parsedArguments);
            if (parsedArguments.length > 0 && parsedArguments[parsedArguments.length - 1] == null)
                continue;
            return Optional.of(closestSubCommand);
        }
        return Optional.ofNullable(closestSubCommand);
    }

    public static boolean isMoreOrEqualNullObjectsInSecondArray(ParsedArgument<?>[] firstArray, ParsedArgument<?>[] secondArray, int givenArguments) {
        int first = 0, second = 0;
        for (int i = 0; i < givenArguments; i++) {
            if (firstArray.length <= i) {
                first++;
            } else {
                if (firstArray[i] == null || firstArray[i].getResult() == null)
                    first++;
            }
            if (secondArray.length <= i) {
                second++;
            } else {
                if (secondArray[i] == null || secondArray[i].getResult() == null)
                    second++;
            }
        }
        return second >= first;
    }

    public static ParsedArgument<?> containsNullObjects(ParsedArgument<?>[] parsedArguments) {
        for (int i = 0; i < parsedArguments.length; i++) {
            ParsedArgument<?> argument = parsedArguments[i];
            if (argument == null)
                return new ParsedArgument<>(i, null);
            if (argument.getResult() == null)
                return argument;
        }
        return null;
    }

    private void showMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            Drapuria.IMPLEMENTATION.sendActionBar((Player) sender, message);
            return;
        }
        sender.sendMessage(message);
    }

}