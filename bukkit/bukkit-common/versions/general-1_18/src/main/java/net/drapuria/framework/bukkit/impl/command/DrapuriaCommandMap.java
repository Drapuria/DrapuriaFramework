/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command;

import com.google.common.collect.ImmutableSet;
import net.drapuria.framework.bukkit.impl.command.meta.BukkitCommandMeta;
import net.drapuria.framework.bukkit.impl.command.meta.BukkitSubCommandMeta;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameter;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameterData;
import net.drapuria.framework.bukkit.impl.command.provider.BukkitCommandProvider;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R1.command.CraftCommandMap;
import org.bukkit.entity.Player;

import java.util.*;

@CommandMapImpl
public class DrapuriaCommandMap extends CraftCommandMap implements ICommandMap {

    private final BukkitCommandProvider commandProvider;

    private static final List<String> EMPTY_ARRAY_LIST = new ArrayList<>();
    private static final String[] emptyStringArray = new String[]{};

    public DrapuriaCommandMap(Server server, BukkitCommandProvider commandProvider) {
        super(server);
        this.commandProvider = commandProvider;

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        return this.tabComplete(sender, cmdLine, null);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine, Location location) {
        if (!(sender instanceof Player)) return Collections.emptyList();
        final Player player = (Player) sender;
        final Set<String> completions = new HashSet<>();
        try {
            boolean doneHere = false;
            final String inputString = cmdLine.toLowerCase();
            final String[] input = cmdLine.split(" ");
            final String mainCommand = input[0] + " ";
            String subCommands = cmdLine.replaceFirst(mainCommand, "");
            final int index = input.length; //run integer to get current location of our string
            final int spaceIndex = cmdLine.indexOf(" ");
            // loop through every command
            commandLoop:
            for (final DrapuriaCommand drapuriaCommand : commandProvider.getCommandRepository().getCommands()) {
                if (!drapuriaCommand.canAccess(player)) continue;
                final BukkitCommandMeta meta = drapuriaCommand.getCommandMeta();
                // loop through all command aliases if we have the permission to use this command
                for (final String command : meta.getCommandAliases()) {
                    if (spaceIndex < 0
                            && inputString.length() < command.length() && StringUtils.startsWithIgnoreCase(
                            command,
                            inputString)) {  //If we are in the main command (no space), we add the all commands to the completion
                        completions.add("/" + command.toLowerCase());
                        continue;
                    }

                    //check if our input starts with command
                    if (!inputString.startsWith(command.toLowerCase() + " ")) {
                        continue;
                    }
                    if (drapuriaCommand.getCommandMeta().getParameterData() != null) {

                        // check if there is paramter left to complete
                        BukkitParameterData parameterData = drapuriaCommand.getCommandMeta().getParameterData();
                        if (parameterData.getParameterCount() > 0) {
                            int parameterIndex = index;
                            if (parameterIndex == parameterData.getParameterCount()
                                    || !cmdLine.endsWith(" ")) {
                                parameterIndex = parameterIndex - 2;
                            }
                            if (parameterIndex < 0)
                                parameterIndex = 0;
                            if (parameterData.getParameterCount() <= parameterIndex
                                    && parameterData.get(parameterData.getParameterCount() - 1).isWildcard()) {
                                completions.addAll(tabCompleteParameter(player,
                                        input[index - 1].toLowerCase(),
                                        Player.class,
                                        emptyStringArray));
                                doneHere = true;
                                continue; // ?
                                //  break commandLoop;
                            }
                            if (parameterData.getParameterCount() > parameterIndex) {
                                BukkitParameter parameter = parameterData.get(parameterIndex);
                                List<String> tabCompletions = tabCompleteParameter(player,
                                        cmdLine.endsWith(" ") ? "" : input[index - 1],
                                        parameter.getClassType(), parameter.getTabCompleteFlags());
                                if (tabCompletions != null) {
                                    completions.addAll(tabCompletions);
                                    doneHere = true;
                                }
                            }
                        }
                    }
                    // loop through all subcommands and checks if player can access the sub command
                    subCommandMeta:
                    for (BukkitSubCommandMeta subCommand : drapuriaCommand.getCommandMeta().getSubCommandMeta()
                            .values()) {
                        if (!subCommand.canAccess(player)) {
                            continue;
                        }
                        // loop through all subcommand aliases
                        for (String subCommandAlias : subCommand.getAliases()) {
                            subCommandAlias = subCommandAlias.toLowerCase();
                            String[] argumentSplit = subCommandAlias.split(" ");
                            final BukkitParameterData parameterData = subCommand.getParameterData();
                            // check if sender has entered the command
                            if (StringUtils.startsWithIgnoreCase(subCommandAlias, subCommands)
                                    || StringUtils.startsWithIgnoreCase(subCommands, subCommandAlias)) {
                                // check if there are parameters left to complete
                                if (subCommands.toLowerCase().startsWith(subCommandAlias.toLowerCase() + " ")
                                        && parameterData.getParameterCount() > 0) {
                                    int parameterIndex = index - argumentSplit.length;
                                    if (parameterIndex == subCommand.getParameterData().getParameterCount()) {
                                        parameterIndex = parameterIndex - (cmdLine.endsWith(" ") ? 2 : 1);
                                    } else {
                                        parameterIndex = parameterIndex - (cmdLine.endsWith(" ") ? 1 : 2);
                                    }
                                    if (parameterIndex < 0)
                                        parameterIndex = 0;
                                    if (parameterData.getParameterCount() <= parameterIndex
                                            && parameterData.get(parameterData.getParameterCount() - 1).isWildcard()) {
                                        completions.addAll(tabCompleteParameter(player,
                                                input[index - 1].toLowerCase(),
                                                Player.class,
                                                emptyStringArray));
                                        doneHere = true;
                                        continue; // ?
                                        //  break commandLoop;
                                    }
                                    if (parameterData.getParameterCount() <= parameterIndex) {
                                        doneHere = true;
                                        continue subCommandMeta;
                                    }
                                    BukkitParameter parameter = parameterData.get(parameterIndex);
                                    List<String> tabCompletions = tabCompleteParameter(player,
                                            cmdLine.endsWith(" ") ? "" : input[index - 1],
                                            parameter.getClassType(), parameter.getTabCompleteFlags());
                                    if (tabCompletions != null) {
                                        completions.addAll(tabCompletions);
                                        doneHere = true;
                                    }
                                    continue subCommandMeta;
                                }
                                int finalIndex = index - 1;
                                if (--finalIndex > parameterData.getParameterCount()) {
                                    continue subCommandMeta;
                                }
                                // get missing aliases
                                final String missing = subCommandAlias.replaceFirst(subCommands, "");
                                // split missing string into parts
                                final String[] missingParts = missing.split(" ");
                                // get real arguments
                                final String[] realArguments = subCommandAlias.split(" ");
                                // get the missing parts
                                final String toComplete = missingParts[0];
                                // get the realarguments
                                if (toComplete.isEmpty())
                                    continue subCommandMeta;
                                for (String m : realArguments) {
                                    if (StringUtils.endsWithIgnoreCase(m, toComplete)
                                            || StringUtils.endsWithIgnoreCase(toComplete, m)) {
                                        completions.add(m);
                                        continue subCommandMeta;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            List<String> completionList = new ArrayList<>(completions);
            // check if we have to go through bukkit completions & check if the players has permission to go through every command
            if (!doneHere && player.hasPermission("drapuria.command.tabcomplete.all")) {
                List<String> vanillaCompletionList = super.tabComplete(sender, cmdLine, (null));
                if (vanillaCompletionList != null)
                    completionList.addAll(vanillaCompletionList);
            }
            completionList.sort(Comparator.comparingInt(String::length));
            return completionList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY_ARRAY_LIST;
    }

    public List<String> tabCompleteParameter(Player sender, String parameter, Class<?> transformTo, String[] tabCompleteFlags) {
        return !commandProvider.getCommandTypeParameterParser().containsKey(transformTo)
                ? (new ArrayList<>()) : commandProvider.getTypeParameter(transformTo)
                .tabComplete(sender, ImmutableSet.copyOf(tabCompleteFlags), parameter.toLowerCase());
    }

    @Override
    public void unregisterDrapuriaCommand(Command command) {
        command.unregister(this);
    }
}
