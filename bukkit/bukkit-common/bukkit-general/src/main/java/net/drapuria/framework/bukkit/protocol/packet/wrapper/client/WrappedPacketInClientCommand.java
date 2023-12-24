package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;

@AutowiredWrappedPacket(value = PacketType.Client.CLIENT_COMMAND, direction = PacketDirection.READ)
@Getter
public final class WrappedPacketInClientCommand extends WrappedPacket {
    private static Class<?> packetClass;
    private static Class<?> enumClientCommandClass;

    private ClientCommand clientCommand;

    public WrappedPacketInClientCommand(Object packet) {
        super(packet);
    }

    public static void init() {
        packetClass = PacketTypeClasses.Client.CLIENT_COMMAND;

        try {
            enumClientCommandClass = NMS_CLASS_RESOLVER.resolve("EnumClientCommand");
        } catch (ClassNotFoundException e) {
            //Probably a subclass
            try {
                enumClientCommandClass = NMS_CLASS_RESOLVER.resolve(packetClass.getSimpleName() + "$EnumClientCommand", "network.protocol.game.PacketPlayInClientCommand$EnumClientCommand");
            } catch (ClassNotFoundException ex) {
                throw new IllegalStateException("Cound't find EnumClientCommand class!", ex);
            }
        }
    }

    @Override
    public void setup() {
        Object enumObj = readObject(0, enumClientCommandClass);
        this.clientCommand = ClientCommand.valueOf(enumObj.toString());
    }

    public enum ClientCommand {
        PERFORM_RESPAWN,
        REQUEST_STATS,
        OPEN_INVENTORY_ACHIEVEMENT
    }

}
