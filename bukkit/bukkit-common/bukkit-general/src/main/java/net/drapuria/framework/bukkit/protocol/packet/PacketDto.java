package net.drapuria.framework.bukkit.protocol.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;

@Getter
@RequiredArgsConstructor
public class PacketDto {

    private final WrappedPacket packet;
    private boolean refresh;

    public PacketDto refresh() {
        this.refresh = true;
        return this;
    }

    public <T extends WrappedPacket> T wrap(Class<T> type) {
        return this.packet.wrap(type);
    }

}
