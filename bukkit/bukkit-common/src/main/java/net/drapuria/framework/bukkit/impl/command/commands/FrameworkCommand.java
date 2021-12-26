package net.drapuria.framework.bukkit.impl.command.commands;

import net.drapuria.framework.annotations.NewInstance;
import net.drapuria.framework.bukkit.impl.annotations.UseFrameworkPlugin;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.plugin.DrapuriaStandalonePlugin;
import net.drapuria.framework.command.annotations.Command;
import net.drapuria.framework.command.annotations.SubCommand;
import net.drapuria.framework.module.service.ModuleService;
import org.bukkit.entity.Player;

@Command(names = "drapuria", useSubCommandsOnly = false)
public class FrameworkCommand extends DrapuriaCommand {

    private final DrapuriaStandalonePlugin plugin;
    private final ModuleService moduleService;

    @NewInstance
    @UseFrameworkPlugin
    public FrameworkCommand(DrapuriaStandalonePlugin plugin, ModuleService moduleService) {
        super("drapuria");
        this.plugin = plugin;
        this.moduleService = moduleService;
    }

    @SubCommand(names = "test", parameters = "{Spieler}")
    public void testCommand(Player player, Player target) {
        player.sendMessage("Hi " + target.getName());
    }

    @SubCommand(names = "plugin", parameters = "")
    public void pluginCommand(Player player) {
        player.sendMessage("Plugin implementation: " + plugin.getName());
    }

    @SubCommand(names = "module", parameters = "")
    public void moduleCommand(Player player) {
        player.sendMessage("Module Service: " + moduleService.toString());
    }

    @Override
    public void execute(Player player) {
        player.sendMessage("Hallo du!");
    }
}
