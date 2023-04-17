package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.ImmutableBiMap;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.protocollib.ProtocolLibService;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.PacketWrapper;
import org.bukkit.scoreboard.DisplaySlot;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;

@AutowiredWrappedPacket(value = PacketType.Server.SCOREBOARD_DISPLAY_OBJECTIVE, direction = PacketDirection.WRITE)
@Getter
@Setter
public class WrappedPacketOutScoreboardDisplayObjective extends WrappedPacket implements SendableWrapper {

    private static ImmutableBiMap<DisplaySlot, Integer> DISPLAY_SLOT_TO_ID;

    public static void init() {

        DISPLAY_SLOT_TO_ID = ImmutableBiMap.<DisplaySlot, Integer>builder()
                .put(DisplaySlot.PLAYER_LIST, 0)
                .put(DisplaySlot.SIDEBAR, 1)
                .put(DisplaySlot.BELOW_NAME, 2)
                .build();

    }

    private DisplaySlot displaySlot;
    private String objective;

    public WrappedPacketOutScoreboardDisplayObjective(Object packet) {
        super(packet);
    }

    public WrappedPacketOutScoreboardDisplayObjective(DisplaySlot displaySlot, String objective) {
        this.displaySlot = displaySlot;
        this.objective = objective;
    }

    @Override
    protected void setup() {
        this.displaySlot = DISPLAY_SLOT_TO_ID.inverse().get(readInt(0));
        this.objective = readString(0);
    }

    @Override
    public Object asNMSPacket() {
        return new PacketWrapper(PacketTypeClasses.Server.SCOREBOARD_DISPLAY_OBJECTIVE)
                .setFieldByIndex(int.class, 0, DISPLAY_SLOT_TO_ID.get(this.displaySlot))
                .setFieldByIndex(String.class, 0, this.objective)
                .getPacket();
    }

    public PacketContainer asProtocolLibPacketContainer() {
        final PacketContainer packetContainer = ProtocolLibService.getService.getProtocolManager().createPacket(com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        packetContainer.getIntegers().write(0, DISPLAY_SLOT_TO_ID.get(this.displaySlot));
        packetContainer.getStrings().write(0, this.objective);
        return packetContainer;
    }
}
