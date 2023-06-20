package net.drapuria.framework.bukkit.impl.command.tabcompletion;

import com.google.common.collect.ImmutableSet;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.impl.command.ICommandMap;
import net.drapuria.framework.bukkit.impl.command.executor.BukkitExecutorData;
import net.drapuria.framework.bukkit.impl.command.meta.BukkitCommandMeta;
import net.drapuria.framework.bukkit.impl.command.meta.BukkitSubCommandMeta;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameter;
import net.drapuria.framework.bukkit.impl.command.provider.BukkitCommandProvider;
import net.drapuria.framework.command.parameter.ParameterData;
import net.drapuria.framework.util.Stacktrace;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DrapuriaTabCompletion {

    private static final List<String> EMPTY_ARRAY_LIST = new ArrayList<>();
    private static final String[] emptyStringArray = new String[]{};
    private static final String EMPTY_STRING = "";

    private final BukkitCommandProvider commandProvider;
    private final ICommandMap spigotCommandMap;


    public DrapuriaTabCompletion(BukkitCommandProvider commandProvider, ICommandMap spigotCommandMap) {
        this.commandProvider = commandProvider;
        this.spigotCommandMap = spigotCommandMap;
    }

    public List<String> tabComplete(CommandSender sender, String cmdLine, Location location) {
        try {
            final Set<String> completions = new HashSet<>();
            boolean addSpigotCompletions = true;
            boolean doneHere = false;
            final String inputString = cmdLine.toLowerCase();
            String[] arguments = inputString.split(" ");
            final String mainCommand = arguments[0];
            String subCommands = inputString.replaceFirst(mainCommand, "");
            int index = subCommands.isEmpty() ? -1 : arguments.length - 1;
            if (index == -1) {
                return this.spigotCommandMap.spigotTabComplete(sender, cmdLine, location);
            }
            arguments = subCommands.replaceFirst(" ", "").split(" ");
            if (!subCommands.endsWith(" "))
                index--;
            final String currentArgument = arguments.length == index ? EMPTY_STRING : arguments[index];
            subCommands = subCommands.replaceFirst(" ", "");
            for (DrapuriaCommand drapuriaCommand : this.commandProvider.getCommandRepository().getCommands()) {
                if (!drapuriaCommand.canAccess(sender))
                    continue;
                if (!drapuriaCommand.getName().equals(mainCommand) && !drapuriaCommand.getAliases().contains(mainCommand))
                    continue;
                executorData:
                for (BukkitExecutorData executorData : drapuriaCommand.getCommandMeta().getExecutorData()) {
                    if (!executorData.isValidLabel(mainCommand) || !executorData.canAccess(sender) || executorData.getParameterData().getParameterCount() == 0)
                        continue executorData;
                    if (executorData.getParameterData().getParameterCount() <= index) {
                        if (executorData.getParameterData().getParameterCount() > 0 && executorData.getParameterData().get(executorData.getParameterData().getParameterCount() - 1).isWildcard()) {
                            completions.addAll(tabCompleteParameter(sender,
                                    currentArgument,
                                    Player.class,
                                    emptyStringArray));

                        } else {
                            addSpigotCompletions = false;
                        }
                        // DONE HERE?
                        continue executorData;
                    }
                    final BukkitParameter bukkitParameter = executorData.getParameterData().get(index);
                    List<String> results = tabCompleteParameter(sender, currentArgument, bukkitParameter.getClassType(), bukkitParameter.getTabCompleteFlags());
                    if (results != null)
                        completions.addAll(results);
                    addSpigotCompletions = false;
                }
                subCommandMeta:
                for (BukkitSubCommandMeta subCommand : drapuriaCommand.getCommandMeta().getSubCommandMetaCollection()) {
                    if (!subCommand.canAccess(sender) || !subCommand.isValidLabel(mainCommand))
                        continue subCommandMeta;
                    final ParameterData<BukkitParameter> parameterData = subCommand.getExecutorData().getParameterData();
                    subCommandAlias:
                    for (String subCommandAliases : subCommand.getExecutorData().getAliases()) {
                        final String normalizedSubCommandAlias = subCommandAliases;
                        if (!normalizedSubCommandAlias.startsWith(subCommands) && !subCommands.startsWith(normalizedSubCommandAlias))
                            continue subCommandAlias;
                        //final String tmpSubCommands = subCommands.endsWith(" ") ? subCommands.index
                        final String[] argumentSplit = subCommandAliases.split(" ");

                        if (argumentSplit.length > index) {
                            final String currentIndex = argumentSplit[index];
                            if (StringUtils.startsWith(currentIndex, currentArgument)) {
                                completions.add(currentIndex);
                                addSpigotCompletions = false;
                            }
                        } else {
                            final int indexOffset = index - argumentSplit.length;
                            System.out.println("indexOffset: " + indexOffset);
                            if (parameterData.getParameterCount() <= indexOffset) {
                                if (parameterData.getParameterCount() > 0 && parameterData.get(parameterData.getParameterCount() - 1).isWildcard()) {
                                    completions.addAll(tabCompleteParameter(sender,
                                            currentArgument,
                                            Player.class,
                                            emptyStringArray));

                                } else {
                                    addSpigotCompletions = false;
                                }
                                // DONE HERE?
                                continue subCommandMeta;
                            }
                            final BukkitParameter bukkitParameter = parameterData.get(indexOffset);
                            final List<String> results = tabCompleteParameter(sender, currentArgument, bukkitParameter.getClassType(), bukkitParameter.getTabCompleteFlags());
                            if (results != null)
                                completions.addAll(results);
                            addSpigotCompletions = false;
                        }
                    }
                }
            }
            if (!doneHere && addSpigotCompletions) {
                completions.addAll(this.spigotCommandMap.spigotTabComplete(sender, cmdLine, location));
            }
            return new ArrayList<>(completions);
        } catch (Exception e) {
            Stacktrace.print(e);
            return EMPTY_ARRAY_LIST;
        }
    }

    private List<String> tabCompleteParameter(CommandSender sender, String parameter, Class<?> transformTo, String[] tabCompleteFlags) {
        if (sender instanceof Player) {
            return !commandProvider.getCommandTypeParameterParser().containsKey(transformTo)
                    ? (new ArrayList<>()) : commandProvider.getTypeParameter(transformTo)
                    .tabComplete((Player) sender, ImmutableSet.copyOf(tabCompleteFlags), parameter.toLowerCase());
        } else {
            return !commandProvider.getCommandTypeParameterParser().containsKey(transformTo)
                    ? (new ArrayList<>()) : commandProvider.getTypeParameter(transformTo)
                    .tabCompleteNonPlayer(sender, ImmutableSet.copyOf(tabCompleteFlags), parameter.toLowerCase());
        }
    }
}
