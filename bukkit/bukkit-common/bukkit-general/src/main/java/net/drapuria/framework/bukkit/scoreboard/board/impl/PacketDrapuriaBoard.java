/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard.board.impl;

import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutScoreboardDisplayObjective;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutScoreboardObjective;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutScoreboardScore;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutScoreboardTeam;
import net.drapuria.framework.bukkit.protocol.protocollib.ProtocolLibService;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.scoreboard.SidebarOptions;
import net.drapuria.framework.bukkit.scoreboard.board.DrapuriaBoard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

/**
 * This class uses ProtocolLib to send Packets
 */
@SuppressWarnings("unchecked")
public class PacketDrapuriaBoard extends DrapuriaBoard {

    private static Class<? extends Enum> healthDisplayEnum;
    private static ProtocolLibService protocolService = ProtocolLibService.getService;


    static {
        healthDisplayEnum = Minecraft.getHealthDisplayTypeClass();
    }

    private boolean created = false;

    public PacketDrapuriaBoard(SidebarOptions options, Player player, String title) {
        super(options, player, title);
    }

    @Override
    public void createBoard() {
        WrappedPacketOutScoreboardObjective packetA = new WrappedPacketOutScoreboardObjective(
                player.getName(),
                "Objective",
                WrappedPacketOutScoreboardObjective.HealthDisplayType.INTEGER,
                WrappedPacketOutScoreboardObjective.Action.ADD
        );
        WrappedPacketOutScoreboardDisplayObjective packetB = new WrappedPacketOutScoreboardDisplayObjective(
                DisplaySlot.SIDEBAR,
                player.getName()
        );        packetB.setDisplaySlot(DisplaySlot.SIDEBAR);
        protocolService.sendPacket(player, packetA.asProtocolLibPacketContainer());
        protocolService.sendPacket(player, packetB.asProtocolLibPacketContainer());
        created = true;
    }

    @Override
    public void setTitle(String title) {
        if (title == null)
            title = "";
        if (this.title.equals(title))
            return;
        super.setTitle(title);
        if (!created)
            createBoard();
        WrappedPacketOutScoreboardObjective packetA = new WrappedPacketOutScoreboardObjective();
        packetA.setName(player.getName());
        packetA.setDisplayName(title);
        packetA.setAction(WrappedPacketOutScoreboardObjective.Action.CHANGED);
        packetA.setHealthDisplayType(WrappedPacketOutScoreboardObjective.HealthDisplayType.INTEGER);
        protocolService.sendPacket(player, packetA.asProtocolLibPacketContainer());
    }

    @Override
    public void sendLine(int line, String team, String entry, String prefix, String suffix) {
        if (!created)
            createBoard();
        WrappedPacketOutScoreboardTeam packet = getOrRegisterTeam(line);
        packet.setPrefix(prefix);
        packet.setSuffix(suffix);
        ProtocolLibService.getService.sendPacket(player, packet.asProtocolLibPacketContainer());
    }

    @Override
    public void sendClear(int line, String entry) {

        WrappedPacketOutScoreboardScore packetA = new WrappedPacketOutScoreboardScore(
                getEntry(line),
                player.getName(),
                line,
                WrappedPacketOutScoreboardScore.ScoreboardAction.REMOVE
        );
        WrappedPacketOutScoreboardTeam packetB = getOrRegisterTeam(line);
        packetB.setAction(1);
        ProtocolLibService.getService.sendPacket(player, packetA.asProtocolLibPacketContainer());
        ProtocolLibService.getService.sendPacket(player, packetB.asProtocolLibPacketContainer());
    }

    @Override
    public void sendDestroy() {
        WrappedPacketOutScoreboardObjective packetA = new WrappedPacketOutScoreboardObjective();
        packetA.setAction(WrappedPacketOutScoreboardObjective.Action.REMOVE);
        packetA.setName(player.getName());
        WrappedPacketOutScoreboardDisplayObjective packetB = new WrappedPacketOutScoreboardDisplayObjective(DisplaySlot.SIDEBAR, player.getName());
        ProtocolLibService.getService.sendPacket(player, packetB.asProtocolLibPacketContainer());
        ProtocolLibService.getService.sendPacket(player, packetA.asProtocolLibPacketContainer());
        created = false;
    }

    private WrappedPacketOutScoreboardTeam getOrRegisterTeam(int line) {

        WrappedPacketOutScoreboardTeam packetB = WrappedPacketOutScoreboardTeam.builder()
                .name("-sb" + line)
                .action(0)
                .chatFormat(0)
                .build();


        if (getLines()[line] != null) {
            packetB.setAction(2);
        } else {
            getLines()[line] = "";
            WrappedPacketOutScoreboardScore packetA = new WrappedPacketOutScoreboardScore(getEntry(line),
                    player.getName(),
                    line, WrappedPacketOutScoreboardScore.ScoreboardAction.CHANGE);
            packetB.setAction(0);
            packetB.getNameSet().add(getEntry(line));
            ProtocolLibService.getService.sendPacket(player, packetA.asProtocolLibPacketContainer());

        }
        return packetB;
    }

}
