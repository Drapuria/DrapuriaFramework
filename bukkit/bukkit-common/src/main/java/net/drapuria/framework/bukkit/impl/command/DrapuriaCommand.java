package net.drapuria.framework.bukkit.impl.command;

import net.drapuria.framework.bukkit.impl.command.meta.BukkitCommandMeta;
import net.drapuria.framework.bukkit.impl.command.meta.BukkitSubCommandMeta;
import net.drapuria.framework.command.FrameworkCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DrapuriaCommand extends Command implements FrameworkCommand<BukkitCommandMeta> {
    private BukkitCommandMeta commandMeta;

    protected DrapuriaCommand(String name) {
        super(name);
        this.commandMeta = new BukkitCommandMeta(this);
    }

    public void execute(Player player, String[] arguments) {
        if (arguments.length == 0) {
            if (this.commandMeta.isUseUnlySubCommands())
                player.sendMessage(generateDefaultUsage(null, ""));
            else
                execute(player);
            return;
        }
        final String cmdLine = Strings.join(arguments, " ");
        final StringBuilder actualCommand = new StringBuilder();
        Map<BukkitSubCommandMeta, String[]> objects = new HashMap<>();
        for (final String argument : arguments) {
            if (actualCommand.length() > 0)
                actualCommand.append(" ");
            actualCommand.append(argument);
            BukkitSubCommandMeta subCommandMeta = this.commandMeta.getSubCommandMeta(actualCommand.toString());
            if (subCommandMeta != null) {
                String[] array = cmdLine.replaceFirst(actualCommand.toString(), "").split(" ");
                objects.put(subCommandMeta, array);
            }
        }

        if (objects.isEmpty()) {
            if (this.commandMeta.isUseUnlySubCommands())
                player.sendMessage(generateDefaultUsage(null, ""));
            else
                execute(player);
        } else {
            objects.entrySet().stream().max(Comparator.comparingInt(value -> value.getValue().length)).ifPresent(entry -> {
                entry.getKey().execute(player, entry.getValue());
            });
        }
    }

    public void execute(Player player) { }

    @Override
    public BukkitCommandMeta getCommandMeta() {
        return this.commandMeta;
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

    private String generateDefaultUsage(BukkitSubCommandMeta subCommand, String label) {
        if (subCommand == null) {
            StringBuilder builder = new StringBuilder();
            AtomicInteger index = new AtomicInteger();

            this.commandMeta.getSubCommandMeta().forEach((s, subCommandMeta) -> {
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
