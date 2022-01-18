package net.drapuria.framework.bukkit.impl.command.commands;

import net.drapuria.framework.bukkit.impl.annotation.UseFrameworkPlugin;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.annotations.NewInstance;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.SubCommand;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.service.ModuleService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

@Command(names = {"drapuria", "marinus"}, useSubCommandsOnly = false)
public class FrameworkCommand extends DrapuriaCommand {

    private final Plugin plugin;
    private final ModuleService moduleService;

    @NewInstance
    @UseFrameworkPlugin
    public FrameworkCommand(Plugin plugin, ModuleService moduleService) {
        super();
        this.plugin = plugin;
        this.moduleService = moduleService;
    }

    @SubCommand(names = "test", parameters = "{Spieler}") // das geht
    public void testCommand(Player player, Player target) {
        player.sendMessage("Hi " + target.getName());
    }

    @SubCommand(names = "aspire beta", parameters = "{Spieler}") // das geht
    public void test2Command(Player player, Player target) {
        player.sendMessage("test 2 " + target.getName());
    }

    @SubCommand(names = "aspire beta test", parameters = "{Spieler}") // das geht
    public void test3Command(Player player, Player target) {
        player.sendMessage("test 3 " + target.getName());
    }

    @SubCommand(names = "aspire hallo test", parameters = "{Spieler}") // das geht
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

    @SubCommand(names = "findmodule", parameters = "{Module}")
    public void findModuleCommand(Player player, String module) {
        Optional<ModuleAdapter> moduleAdapter = this.moduleService.getGlobalModuleRepository().findById(module);
        if (moduleAdapter.isPresent()) {
            player.sendMessage("Found Module " + moduleAdapter.get());
        } else {
            player.sendMessage("Found no Module with name " + module);
        }
    }

    @Override // das hier?
    public void execute(Player player) {
        player.sendMessage("Hallo du!");
    }
}
