package net.drapuria.framework.bukkit.impl.command.context;

import net.drapuria.framework.bukkit.impl.command.meta.BukkitCommandMeta;
import net.drapuria.framework.command.FrameworkCommand;
import net.drapuria.framework.command.context.ParsedArgument;
import org.bukkit.command.ConsoleCommandSender;

public class ConsoleCommandSenderCommandContext extends BukkitCommandContext<ConsoleCommandSender>{
    public ConsoleCommandSenderCommandContext(ConsoleCommandSender source, String input, String label,
                                              FrameworkCommand<BukkitCommandMeta> command, String[] unparsedArguments, ParsedArgument<?>[] arguments) {
        super(source, input, label, command, unparsedArguments, arguments);
    }
}
