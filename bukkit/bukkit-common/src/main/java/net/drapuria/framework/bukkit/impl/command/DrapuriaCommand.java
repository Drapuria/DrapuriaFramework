package net.drapuria.framework.bukkit.impl.command;

import net.drapuria.framework.bukkit.impl.command.meta.BukkitCommandMeta;
import net.drapuria.framework.bukkit.impl.command.meta.BukkitSubCommandMeta;
import net.drapuria.framework.command.FrameworkCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
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
        String baseCommandInput = arguments[0];
        BukkitSubCommandMeta subCommandMeta = this.commandMeta.getSubCommandMeta(baseCommandInput);

        if (subCommandMeta == null) {
            player.sendMessage(generateDefaultUsage(null, baseCommandInput));
            return;
        }

        String permissionString = subCommandMeta.getCommandPermission();

        if (!player.hasPermission(permissionString)) {
            player.sendMessage(this.generateDefaultPermission());
            return;
        }
        StringBuilder builder = new StringBuilder();

        for (String args : arguments) {
            builder.append(args);

            String[] array = Arrays.copyOfRange(arguments, 1, arguments.length);
            if (!this.commandMeta.getSubCommandMeta(builder.toString()).execute(player, array))
                player.sendMessage(generateDefaultUsage(subCommandMeta, baseCommandInput));
            break;
        }
    }

    public void execute(Player player) {
    }

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
