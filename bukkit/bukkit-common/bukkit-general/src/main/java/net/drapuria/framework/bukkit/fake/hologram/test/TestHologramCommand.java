package net.drapuria.framework.bukkit.fake.hologram.test;

import com.google.common.collect.ImmutableList;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.fake.FakeShowType;
import net.drapuria.framework.bukkit.fake.hologram.GlobalHologram;
import net.drapuria.framework.bukkit.fake.hologram.HologramService;
import net.drapuria.framework.bukkit.fake.hologram.PlayerHologram;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramHelper;
import net.drapuria.framework.bukkit.fake.hologram.line.ItemLine;
import net.drapuria.framework.bukkit.fake.hologram.line.TextLine;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.item.ItemBuilder;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.command.annotation.Command;
import net.drapuria.framework.command.annotation.CommandParameter;
import net.drapuria.framework.command.annotation.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

@Command(names = {"hologram", "holo"}, permission = "drapuria.command.hologram", description = "Lets you create holograms")
public class TestHologramCommand extends DrapuriaCommand {

    private final HologramService hologramService;

    public TestHologramCommand(HologramService hologramService) {
        this.hologramService = hologramService;
    }

    @SubCommand(names = "create", parameters = "{Name}{Text}")
    public void createHologram(final DrapuriaPlayer player, final String name, @CommandParameter(wildcard = true, defaultValue = "§holo") String text) {
        if (text.equals("§holo")) text = name;

        final GlobalHologram globalHologram = new GlobalHologram(player.getEyeLocation());
        globalHologram.addLine(new TextLine(HologramHelper.newId(), ChatColor.translateAlternateColorCodes('&', text)));
        new BukkitRunnable() {
            @Override
            public void run() {
                globalHologram.addLine(0, new ItemLine(HologramHelper.newId(), HologramHelper.newId(), ItemBuilder.of(Material.DIAMOND).build()));
                globalHologram.refreshLines();
            }
        }.runTaskLater(Drapuria.PLUGIN, 20 * 4);
        globalHologram.setLocationBoundToPlayer(true, player.getPlayer());
        globalHologram.setBoundYOffset(1);
        globalHologram.setType(FakeShowType.EXCLUDING);
        globalHologram.addExcludedOrIncludedPlayer(player.getPlayer());
        this.hologramService.addHologram(globalHologram);
        this.hologramService.checkPlayerBoundHolograms();
        FrameworkMisc.TASK_SCHEDULER.runScheduled(() -> globalHologram.removeExcludedOrIncludedPlayer(player), 20 * 20);
    }

    @SubCommand(names = "clear", parameters = "")
    public void removeHologram(final DrapuriaPlayer player) {
        ImmutableList.copyOf(this.hologramService.getHologramRepository().getGlobalHolograms())
                .forEach(this.hologramService::removeHologram);
    }

    @SubCommand(names = "togglemode", parameters = "")
    public void toggleMode(final DrapuriaPlayer player) {
        this.hologramService.setUseEventsForHologramHandling(!this.hologramService.isUseEventsForHologramHandling());
        player.sendActionBar("Toggled Mode");
    }

    @SubCommand(names = "clear player", parameters = "")
    public void clearPlayers(final DrapuriaPlayer player) {
        ImmutableList.copyOf(this.hologramService.getHologramRepository().getHolograms(player.getPlayer())) // we save the bukkit player object not the drapuria player object
                .forEach(this.hologramService::removeHologram);
        player.sendActionBar("removed holograms?");
    }

    @SubCommand(names = "create player", parameters = "{Name}{Text}")
    public void createPlayerHologram(final DrapuriaPlayer player, final String name, @CommandParameter(wildcard = true, defaultValue = "§holo") String text) {
        if (text.equals("§holo")) text = name;
        final PlayerHologram playerHologram = new PlayerHologram(player.getPlayer(), player.getEyeLocation());
        final TextLine textLine = new TextLine(HologramHelper.newId(), ChatColor.translateAlternateColorCodes('&', text));
        playerHologram.addLine(textLine);
        this.hologramService.addHologram(playerHologram);
        player.sendActionBar("player hologram created");
        new BukkitRunnable() {
            @Override
            public void run() {
                playerHologram.removeLine(textLine);
                player.sendActionBar("removed textline?");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        playerHologram.addLine(textLine);
                        player.sendActionBar("readded textline?");
                    }
                }.runTaskLater(Drapuria.PLUGIN, 20 * 5);
            }
        }.runTaskLater(Drapuria.PLUGIN, 20 * 15);
    }
}
