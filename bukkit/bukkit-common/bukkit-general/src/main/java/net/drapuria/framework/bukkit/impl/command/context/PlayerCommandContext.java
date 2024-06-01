package net.drapuria.framework.bukkit.impl.command.context;

import net.drapuria.framework.bukkit.impl.command.meta.BukkitCommandMeta;
import net.drapuria.framework.command.FrameworkCommand;
import net.drapuria.framework.command.context.ParsedArgument;
import org.bukkit.entity.Player;

public class PlayerCommandContext extends BukkitCommandContext<Player> {

    public PlayerCommandContext(Player source, String input, String label,
                                FrameworkCommand<BukkitCommandMeta> command,
                                String[] unparsedArguments, ParsedArgument<?>[] arguments) {
        super(source, input, label, command, unparsedArguments, arguments);
    }
}
