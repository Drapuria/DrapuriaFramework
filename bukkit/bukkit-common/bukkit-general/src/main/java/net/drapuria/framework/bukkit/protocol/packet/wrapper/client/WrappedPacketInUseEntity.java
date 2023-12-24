package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import lombok.Getter;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import org.bukkit.entity.Entity;

import static net.drapuria.framework.bukkit.protocol.packet.type.PacketType.*;


@Getter
@AutowiredWrappedPacket(value = Client.USE_ENTITY, direction = PacketDirection.READ)
public final class WrappedPacketInUseEntity extends WrappedPacket {

    private static Class<?> ENUM_ENTITY_USE_ACTION;

    private Entity entity;
    private int entityID;
    private EntityUseAction action;
    public WrappedPacketInUseEntity(final Object packet) {
        super(packet);
    }

    public static void init() {
        Class<?> useEntityClass = NMS_CLASS_RESOLVER.resolveSilent("PacketPlayInUseEntity", "network.protocol.game.PacketPlayInUseEntity");
        try {
            ENUM_ENTITY_USE_ACTION = NMS_CLASS_RESOLVER.resolve("EnumEntityUseAction", "network.protocol.game.PacketPlayInUseEntity$EnumEntityUseAction");
        } catch (ClassNotFoundException e) {
            //That is fine, it is probably a subclass
            ENUM_ENTITY_USE_ACTION = NMS_CLASS_RESOLVER.resolveSilent(useEntityClass.getSimpleName() + "$EnumEntityUseAction");
        }
    }

    @Override
    protected void setup() {
        this.entityID = readInt(0);
        if(ENUM_ENTITY_USE_ACTION == null) {
                System.out.println("class is null");
        }
        final Object useActionEnum = readObject(0, ENUM_ENTITY_USE_ACTION);
        this.action = EntityUseAction.valueOf(useActionEnum.toString());
    }

    /**
     * Lookup the associated entity by the ID that was sent in the packet.
     * @return Entity
     */
    public Entity getEntity() {
        if(entity != null) {
            return entity;
        }
        return entity = Drapuria.IMPLEMENTATION.getEntity(this.getWorld(), this.entityID);
    }

    public enum EntityUseAction {
        INTERACT, INTERACT_AT, ATTACK, INVALID
    }
}
