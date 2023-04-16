package net.drapuria.framework.bukkit.protocol.packet.event.type;

import net.drapuria.framework.bukkit.protocol.packet.event.PacketEvent;
import org.bukkit.entity.Player;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;

/**
 * This event is called each time a packet is received from a client.
 */
public final class PacketReceiveEvent extends PacketEvent {
    private final Player player;
    private final Object packet;
    private boolean cancelled;

    public PacketReceiveEvent(final Player player, final Object packet) {
        this.player = player;
        this.packet = packet;
    }

    /**
     * Get the packet sender
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the packet's name (NMS packet class simple name).
     * @return Name of the packet
     * @deprecated It is recommended not to use this.
     */
    @Deprecated
    public String getPacketName() {
        return this.packet.getClass().getSimpleName();
    }

    /**
     * Get the raw packet object
     * @return packet object
     */
    public Object getNMSPacket() {
        return this.packet;
    }

    /**
     * Get the class of the NMS packet object
     * @deprecated It is useless, rather use getNMSPacket().getClass()
     * @return packet object class
     */
    @Deprecated
    public Class<?> getNMSPacketClass() {
        return packet.getClass();
    }

    public WrappedPacket getWrappedPacket() {
        return PacketDirection.READ.getWrappedFromNMS(this.player, this.getPacketId(), this.packet);
    }

    /**
     * Get the ID of the packet
     * @return packet id
     */
    public byte getPacketId() {
        return PacketType.Client.PACKET_IDS.getOrDefault(packet.getClass(), (byte) -1);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
