/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.command.meta.BukkitCommandMeta;
import net.drapuria.framework.bukkit.impl.command.meta.BukkitSubCommandMeta;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.command.FrameworkCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DrapuriaCommand extends Command implements FrameworkCommand<BukkitCommandMeta> {
    private BukkitCommandMeta commandMeta;

    protected DrapuriaCommand() {
        super("name");
        this.commandMeta = new BukkitCommandMeta(this);
    }

    public void execute(Player player, String[] arguments) {
        if (!canAccess(player)) {
            Drapuria.IMPLEMENTATION.sendActionBar(player, generateDefaultPermission());
            return;
        }
        if (arguments.length == 0) {
            if (this.commandMeta.isUseUnlySubCommands())
                player.sendMessage(generateDefaultUsage(null, ""));
            else {
                if (this.commandMeta.getMethod() == null)
                    execute(player);
                else {
                    this.commandMeta.execute(player, arguments);
                }
            }
            return;
        }
        final String cmdLine = String.join(" ", arguments);
        StringBuilder actualCommand = new StringBuilder();
        Map<BukkitSubCommandMeta, String[]> objects = new HashMap<>();
        for (final String argument : arguments) {
            if (actualCommand.length() > 0)
                actualCommand.append(" ");
            actualCommand.append(argument);
            List<BukkitSubCommandMeta> subCommandMeta = this.commandMeta.getValidSubCommandMetas(actualCommand.toString().toLowerCase());
            for (BukkitSubCommandMeta bukkitSubCommandMeta : subCommandMeta) {
                if (bukkitSubCommandMeta != null) {
                    String[] array = Arrays.stream(cmdLine.replaceFirst(actualCommand.toString(), "")
                                    .split(" "))
                            .filter(s -> !s.isEmpty()).toArray(String[]::new);
                    objects.put(bukkitSubCommandMeta, array);
                }
            }
        }
        //System.out.println("ACTUAL COMMAND: " + actualCommand.toString());
        /*
        actualCommand = new StringBuilder();
        for (final String argument : arguments) {
            if (actualCommand.length() > 0)
                actualCommand.append(" ");
            actualCommand.append(argument);
            BukkitSubCommandMeta subCommandMeta = this.commandMeta.getSubCommandMeta(actualCommand.toString().toLowerCase());
            if (subCommandMeta != null) {
                String[] array = Arrays.stream(cmdLine.replaceFirst(actualCommand.toString(), "")
                        .split(" "))
                        .filter(s -> !s.isEmpty()).toArray(String[]::new);
                objects.put(subCommandMeta, array);
            }
        }
         */
        if (objects.isEmpty()) {
            if (this.commandMeta.isUseUnlySubCommands())
                player.sendMessage(generateDefaultUsage(null, ""));
            else {
                if (this.commandMeta.getMethod() == null)
                    execute(player);
                else {
                    this.commandMeta.execute(player, arguments);
                }
            }
        } else {
            Map.Entry<BukkitSubCommandMeta, String[]> subCommandEntry = objects.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().isEveryArgumentPresent(player, entry.getValue()))
                    .max(Comparator.comparingInt(value -> value.getKey().getParameterData().getParameterCount()))
                    .orElse(objects.entrySet()
                            .stream()
                            .max(Comparator.comparingInt(value -> value.getKey().getDefaultAlias().split(" ").length))
                            .orElse(null));
            if (subCommandEntry == null) {
                player.sendMessage("wrong arguments todo"); // TODO DEFAULT VALUE
                return;
            }
            if (subCommandEntry.getKey().isAsyncExecution()) {
                DrapuriaCommon.executorService.execute(() -> subCommandEntry.getKey().execute(player, subCommandEntry.getValue()));
            } else
                    subCommandEntry.getKey().execute(player, subCommandEntry.getValue());
            /*
            objects.entrySet().stream()
                    .max(Comparator.comparingInt(value -> value.getKey().getDefaultAlias().split(" ").length))
                    .ifPresent(entry -> {
                        if (entry.getKey().isAsyncExecution()) {
                            DrapuriaCommon.executorService.execute(() -> entry.getKey().execute(player, entry.getValue()));
                        } else
                            entry.getKey().execute(player, entry.getValue());
                    });
             */
        }
    }

    @Override
    public BukkitCommandMeta getCommandMeta() {
        return this.commandMeta;
    }

    public void execute(Player player) {
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        execute((Player) commandSender, strings);
        return true;
    }

    private String generateDefaultPermission() {
        return "§cDazu hast du keine Berechtigung!";
    }

    public boolean canAccess(Player player) {
        return commandMeta.canAccess(player);
    }

    public void playerNotFound(final DrapuriaPlayer executor, final String player) {
        executor.sendActionBar(String.format(this.getPlayerNotOnlineMessage(), player));
    }

    protected String getPlayerNotOnlineMessage() {
        return "§7%s§c ist nicht online.";
    }


    protected String generateDefaultUsage(BukkitSubCommandMeta subCommand, String label) {
        if (subCommand == null) {
            StringBuilder builder = new StringBuilder();
            AtomicInteger index = new AtomicInteger();

            // TODO PREBUILD PARAMETER STRING AND REPLACE OPTIONAL (EVERYTHING WITH A DEFAULT VALUE OR EVERYTHING WITH MULTIPLE OPTIONS WHERE IT IS THE LARGER THING WITH [argument] INSTEAD OF <argument>
            this.commandMeta.getSubCommandMetaCollection().forEach((subCommandMeta) -> {
                builder.append("Verwendung: /")
                        .append(this.getName())
                        .append(" ")
                        .append(subCommandMeta.getDefaultAlias())
                        .append(" ")
                        .append(subCommandMeta.getParameterString().replace("{", "<")
                                .replace("}", ">"));
                index.getAndIncrement();
                if (index.get() < this.commandMeta.getSubCommandMeta().size())
                    builder.append("\n");
            });

            return builder.toString();
        }
        return "Verwendung: /" + this.getName() + " " + label + " " + subCommand.getSubCommand()
                .parameters()
                .replace("{", "<")
                .replace("}", ">");
    }
}
