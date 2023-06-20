/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command;

import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.command.context.BukkitCommandContext;
import net.drapuria.framework.bukkit.impl.command.context.ConsoleCommandSenderCommandContext;
import net.drapuria.framework.bukkit.impl.command.context.PlayerCommandContext;
import net.drapuria.framework.bukkit.impl.command.meta.BukkitCommandMeta;
import net.drapuria.framework.bukkit.impl.command.meta.BukkitSubCommandMeta;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.command.FrameworkCommand;
import net.drapuria.framework.command.context.ParsedArgument;
import net.drapuria.framework.command.service.CommandService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DrapuriaCommand extends Command implements FrameworkCommand<BukkitCommandMeta> {
    private final CommandService commandService;
    private final BukkitCommandMeta commandMeta;
    @Getter
    private final Object instance;

    public DrapuriaCommand(final CommandService commandService, final Object instance) {
        super(UUID.randomUUID().toString());
        this.instance = instance;
        this.commandService = commandService;
        this.commandMeta = new BukkitCommandMeta(commandService, this);
    }


    @Override
    public BukkitCommandMeta getCommandMeta() {
        return this.commandMeta;
    }


    @Override
    public boolean execute(CommandSender commandSender, String label, String[] arguments) {
        final BukkitCommandContext<? extends CommandSender> commandContext = commandSender instanceof Player ? new PlayerCommandContext((Player) commandSender, String.join(" ", arguments), label, this, arguments, new ParsedArgument[arguments.length]) : new ConsoleCommandSenderCommandContext(Bukkit.getConsoleSender(), String.join(" ", arguments), label, this, arguments, new ParsedArgument[arguments.length]);
        execute(commandContext);
        return true;
    }

    private void execute(BukkitCommandContext<? extends CommandSender> context) {
        if (!commandMeta.canAccess(context.getSource())) {
            if (context instanceof PlayerCommandContext)
                Drapuria.IMPLEMENTATION.sendActionBar((Player) context.getSource(), generateDefaultPermission());
            else
                context.getSource().sendMessage(generateDefaultPermission());
            return;
        }
        commandMeta.execute2(context);
    }


    private String generateDefaultPermission() {
        return "§cDazu hast du keine Berechtigung!";
    }

    public boolean canAccess(CommandSender sender) {
        return commandMeta.canAccess(sender);
    }

    public void playerNotFound(final DrapuriaPlayer executor, final String player) {
        executor.sendActionBar(String.format(this.getPlayerNotOnlineMessage(), player));
    }

    protected String getPlayerNotOnlineMessage() {
        return "§7%s§c ist nicht online.";
    }


    public String generateDefaultUsage(BukkitSubCommandMeta subCommand, String label) {
        if (subCommand == null) {
            StringBuilder builder = new StringBuilder();
            AtomicInteger index = new AtomicInteger();

            // TODO PREBUILD PARAMETER STRING AND REPLACE OPTIONAL (EVERYTHING WITH A DEFAULT VALUE OR EVERYTHING WITH MULTIPLE OPTIONS WHERE IT IS THE LARGER THING WITH [argument] INSTEAD OF <argument>
            this.commandMeta.getSubCommandMetaCollection()
                    .stream().filter(meta -> meta.getExecutorData().isValidLabel(label))
                    .forEach((subCommandMeta) -> {
                        builder.append("Verwendung: /")
                                .append(this.getName())
                                .append(" ")
                                .append(subCommandMeta.getExecutorData().getName())
                                .append(" ")
                                .append(String.join(" ", Arrays.stream(subCommandMeta.getExecutorData().getParameterData().getParameters()).map(s -> "<" + s + ">").toArray(String[]::new)));
                        index.getAndIncrement();
                        if (index.get() < this.commandMeta.getSubCommandMeta().size())
                            builder.append("\n");
                    });
            this.commandMeta.getExecutorData()
                    .stream()
                    .filter(data -> data.isValidLabel(label))
                    .forEach(data -> {
                        if (builder.length() != 0)
                            builder.append("\n");
                        builder.append("Verwendung: /")
                                .append(label)
                                .append(" ")
                                .append(Arrays.stream(data.getParameterData().getParameters()).map(bukkitParameter -> "<" + bukkitParameter.getParameter() + ">").collect(Collectors.joining(" ")));
                    });
            if (builder.length() == 0)
                return "THIS BITCH EMPTY";
            return builder.toString();
        }
        return "Verwendung: /" + this.getName() + " " + label + " " + String.join(" ", Arrays.stream(subCommand.getSubCommand()
                .parameters()).map(s -> "<" + s + ">").toArray(String[]::new));
    }
}
