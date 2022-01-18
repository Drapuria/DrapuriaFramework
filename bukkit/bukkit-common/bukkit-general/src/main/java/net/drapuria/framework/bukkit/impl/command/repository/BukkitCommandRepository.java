package net.drapuria.framework.bukkit.impl.command.repository;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.impl.command.provider.BukkitCommandProvider;
import net.drapuria.framework.command.repository.CommandRepository;
import net.drapuria.framework.module.Module;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;

public class BukkitCommandRepository implements CommandRepository<DrapuriaCommand> {

    private final Set<DrapuriaCommand> commands = new HashSet<>();

    private BukkitCommandProvider commandProvider;

    public void setCommandProvider(BukkitCommandProvider commandProvider) {
        this.commandProvider = commandProvider;
    }

    @Override
    public Set<DrapuriaCommand> getCommands() {
        return this.commands;
    }

    @Override
    public void registerCommand(Object source, DrapuriaCommand command) {
        this.commands.add(command);

        if (this.commandProvider.getDrapuriaCommandMap().getCommand(command.getCommandMeta().getCommandName()) != null) {
            DrapuriaCommon.getLogger().warn("Command " + command.getName() + " is already registered.");
        }
        String prefix;
        if (source instanceof Module) {
            Module module = (Module) source;
            prefix = module.getName().toLowerCase();
        } else if (source instanceof Plugin) {
            Plugin plugin = (Plugin) source;
            prefix = plugin.getName().toLowerCase();
        } else {
            prefix = Drapuria.PLUGIN.getName().toLowerCase();
        }
        this.commandProvider.getDrapuriaCommandMap().register(prefix, command);
    }

    @SneakyThrows
    @Override
    public void unregisterCommand(DrapuriaCommand command) {
        this.commands.remove(command);
        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandProvider.getDrapuriaCommandMap());

        command.unregister(commandProvider.getDrapuriaCommandMap());
        knownCommands.remove(command.getName());
    }

    @Override
    public DrapuriaCommand findByName(String name) {
        return commands.stream().filter(drapuriaCommand -> drapuriaCommand.getCommandMeta()
                .getCommandName()
                .equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
