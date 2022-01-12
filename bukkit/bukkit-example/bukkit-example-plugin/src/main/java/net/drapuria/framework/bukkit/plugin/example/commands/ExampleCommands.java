package net.drapuria.framework.bukkit.plugin.example.commands;

import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.SubCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Command(names = "example", useSubCommandsOnly = false)
public class ExampleCommands extends DrapuriaCommand {

    private final Plugin plugin;

    public ExampleCommands(Plugin plugin) {
        super("example");
        this.plugin = plugin;
    }

    @SubCommand(names = "hello", parameters = "{Spieler}")
    public void helloCommand(Player player, Player target) {
        player.sendMessage("Spieler: " + target.getName());
    }

    @Override
    public void execute(Player player) {
        player.sendMessage("Executed Example Command! Provided by " + plugin.getName());
    }
}
