package net.drapuria.framework.bukkit.impl.command.commands;

import net.drapuria.framework.annotations.NewInstance;
import net.drapuria.framework.bukkit.impl.annotations.UseFrameworkPlugin;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.command.annotations.Command;
import net.drapuria.framework.command.annotations.SubCommand;
import net.drapuria.framework.module.service.ModuleService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Command(names = {"drapuria", "marinus"}, useSubCommandsOnly = false)
public class FrameworkCommand extends DrapuriaCommand {

    private final Plugin plugin;
    private final ModuleService moduleService;

    @NewInstance
    @UseFrameworkPlugin
    public FrameworkCommand(Plugin plugin, ModuleService moduleService) {
        super("drapuria");
        this.plugin = plugin;
        this.moduleService = moduleService;
    }

    @SubCommand(names = "test", parameters = "{Spieler}") // das geht
    public void testCommand(Player player, Player target) {
        player.sendMessage("Hi " + target.getName());
    }

    @SubCommand(names = "aspire beta", parameters = "{Spieler}") // das geht nicht
    public void test2Command(Player player, Player target) {
        player.sendMessage("test 2 " + target.getName());
    }

    @SubCommand(names = "aspire beta test", parameters = "{Spieler}") // das geht nicht
    public void test3Command(Player player, Player target) {
        player.sendMessage("test 3 " + target.getName());
    }

    @SubCommand(names = "aspire hallo test", parameters = "{Spieler}") // das geht nicht
    public void test4Command(Player player, Player target) {
        player.sendMessage("test 4 hallo " + target.getName());
    }

    @SubCommand(names = "plugin", parameters = "") // das geht
    public void pluginCommand(Player player) {
        player.sendMessage("Plugin implementation: " + plugin.getName());
    }

    @SubCommand(names = "module", parameters = "") // das geht
    public void moduleCommand(Player player) {
        player.sendMessage("Module Service: " + moduleService.toString());
    }

    @Override // das hier?
    public void execute(Player player) {
        player.sendMessage("Hallo du!");
    }
}
