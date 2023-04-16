package net.drapuria.framework.bukkit.protocol.packet.event.type;

import net.drapuria.framework.bukkit.protocol.packet.event.PacketEvent;
import org.bukkit.entity.Player;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;

/**
 * This event is called each time the server sends a packet to the client.
 */
public final class PacketSendEvent extends PacketEvent {
    private final Player player;
    private final Object packet;
    private boolean cancelled;

    public PacketSendEvent(final Player player, final Object packet) {
        this.player = player;
        this.packet = packet;
        this.cancelled = false;
    }


    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the packet's name (NMS packet class simple name)
     * @deprecated It is recommended not to use this, as it is an expensive function to call.
     * @return Name of the packet
     */
    @Deprecated
    public String getPacketName() {
        return this.packet.getClass().getSimpleName();
    }

    /**
     * Get the ID of the packet
     *
     * @return packet id
     */
    public byte getPacketId() {
        return PacketType.Server.PACKET_IDS.getOrDefault(packet.getClass(), (byte) -1);
    }

    /**
     * Get the NMS packet object
     *
     * @return packet object
     */
    public Object getNMSPacket() {
        return this.packet;
    }

    public WrappedPacket getWrappedPacket() {
        return PacketDirection.WRITE.getWrappedFromNMS(this.player, this.getPacketId(), this.packet);
    }

    /**
     * Get the class of the NMS packet object.
     * Deprecated because it is useless, rather use getNMSPacket().getClass().
     *
     * @return packet object class
     */
    @Deprecated
    public Class<?> getNMSPacketClass() {
        return packet.getClass();
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

