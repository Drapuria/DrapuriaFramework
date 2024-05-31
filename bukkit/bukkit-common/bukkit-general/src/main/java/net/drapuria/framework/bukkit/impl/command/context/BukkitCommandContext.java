package net.drapuria.framework.bukkit.impl.command.context;

import net.drapuria.framework.bukkit.impl.command.meta.BukkitCommandMeta;
import net.drapuria.framework.command.FrameworkCommand;
import net.drapuria.framework.command.context.CommandContext;
import net.drapuria.framework.command.context.ParsedArgument;
import org.bukkit.command.CommandSender;

public class BukkitCommandContext<T extends CommandSender> extends CommandContext<T, BukkitCommandMeta> {
    public BukkitCommandContext(T source, String input, String label, FrameworkCommand<BukkitCommandMeta> command,
                                String[] unparsedArguments, ParsedArgument<?>[] arguments) {
        super(source, input, label, command, unparsedArguments, arguments);
    }
}
