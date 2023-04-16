package net.drapuria.framework.bukkit.protocol.packet.wrapper.client.blockplace;

import lombok.Getter;
import net.drapuria.framework.bukkit.util.BukkitUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;

@Getter
final class WrappedPacketInBlockPlace_1_9 extends WrappedPacket {
    private Block block;

    public WrappedPacketInBlockPlace_1_9(final Player player, final Object packet) {
        super(player, packet);
    }

    @Override
    protected void setup() {
        this.block = BukkitUtil.getBlockLookingAt(this.getPlayer(), 3);
    }


}
