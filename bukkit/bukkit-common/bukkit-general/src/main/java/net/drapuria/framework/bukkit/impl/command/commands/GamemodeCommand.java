package net.drapuria.framework.bukkit.impl.command.commands;

import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.CommandParameter;
import net.drapuria.framework.command.annotation.Executor;
import net.drapuria.framework.command.context.CommandContext;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import java.util.Set;

//@Command(names = {"gamemode", "gmc", "gma", "gms", "gm"})
public class GamemodeCommand {

    @Executor(labels = {"gamemode", "gm"}, parameters = {"gamemode", "spieler"})
    public void gamemodeCommand(final DrapuriaPlayer player, GameMode gameMode, @CommandParameter(defaultValue = "self") final DrapuriaPlayer target) {
        if (gameMode == null) {
            player.sendActionBar("§c§lFalscher Gamemode");
            return;
        }
        // logic für nachricht senden und ob man others gamemode überhaupt changen kann
        target.setGameMode(gameMode);
    }

    @Executor(labels = "gmc", parameters = "spieler")
    public void gmcCommand( final DrapuriaPlayer player, @CommandParameter(defaultValue = "self") final DrapuriaPlayer target) {
        // logic für nachricht senden und ob man others gamemode überhaupt changen kann
        target.setGameMode(GameMode.CREATIVE);
    }

    @Executor(labels = "gms", parameters = "spieler")
    public void gmsCommand(final DrapuriaPlayer player, @CommandParameter(defaultValue = "self") final DrapuriaPlayer target) {
        // logic für nachricht senden und ob man others gamemode überhaupt changen kann
        target.setGameMode(GameMode.SURVIVAL);
    }

    @Executor(labels = "gma", parameters = "spieler", permission = "test")
    public void gmaCommand(final DrapuriaPlayer player, @CommandParameter(defaultValue = "self") final DrapuriaPlayer target) {
        // logic für nachricht senden und ob man others gamemode überhaupt changen kann
        target.setGameMode(GameMode.ADVENTURE);
    }
}