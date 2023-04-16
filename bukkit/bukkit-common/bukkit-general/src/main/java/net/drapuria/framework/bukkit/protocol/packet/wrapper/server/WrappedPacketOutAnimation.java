package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import org.bukkit.entity.Entity;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@AutowiredWrappedPacket(value = PacketType.Server.ANIMATION, direction = PacketDirection.WRITE)
@Getter
public final class WrappedPacketOutAnimation extends WrappedPacket implements SendableWrapper {

    private static Class<?> animationClass, nmsEntityClass;
    private static Constructor<?> animationConstructor;
    private static Map<Integer, EntityAnimationType> cachedAnimationIDS;
    private static Map<EntityAnimationType, Integer> cachedAnimations;

    private Entity entity;
    private int entityID;
    private EntityAnimationType type;

    public WrappedPacketOutAnimation(final Object packet) {
        super(packet);
    }

    public WrappedPacketOutAnimation(final Entity target, final EntityAnimationType type) {
        super();
        this.entityID = target.getEntityId();
        this.entity = target;
        this.type = type;
    }

    public static void init() {

        animationClass = PacketTypeClasses.Server.ANIMATION;
        try {
            nmsEntityClass = NMS_CLASS_RESOLVER.resolve("Entity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            animationConstructor = animationClass.getConstructor(nmsEntityClass, int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        cachedAnimationIDS = ImmutableMap.<Integer, EntityAnimationType>builder()
                .put(0, EntityAnimationType.SWING_MAIN_ARM)
                .put(1, EntityAnimationType.TAKE_DAMAGE)
                .put(2, EntityAnimationType.LEAVE_BED)
                .put(3, EntityAnimationType.SWING_OFFHAND)
                .put(4, EntityAnimationType.CRITICAL_EFFECT)
                .put(5, EntityAnimationType.MAGIC_CRITICAL_EFFECT)
                .build();

        cachedAnimations = ImmutableMap.<EntityAnimationType, Integer>builder()
                .put(EntityAnimationType.SWING_MAIN_ARM, 0)
                .put(EntityAnimationType.TAKE_DAMAGE, 1)
                .put(EntityAnimationType.LEAVE_BED, 2)
                .put(EntityAnimationType.SWING_OFFHAND, 3)
                .put(EntityAnimationType.CRITICAL_EFFECT, 4)
                .put(EntityAnimationType.MAGIC_CRITICAL_EFFECT, 5)
                .build();
    }

    @Override
    protected void setup() {
        this.entityID = readInt(0);
        int animationID = readInt(1);
        this.type = cachedAnimationIDS.get(animationID);
    }

    /**
     * Lookup the associated entity by the ID that was sent in the packet.
     *
     * @return Entity
     */
    public Entity getEntity() {
        return Drapuria.IMPLEMENTATION.getEntity(this.entityID);
    }

    @Override
    public Object asNMSPacket() {
        final Object nmsEntity = Minecraft.getHandleSilent(this.entity);
        final int index = cachedAnimations.get(type);
        try {
            return animationConstructor.newInstance(nmsEntity, index);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum EntityAnimationType {
        SWING_MAIN_ARM, TAKE_DAMAGE, LEAVE_BED, SWING_OFFHAND, CRITICAL_EFFECT, MAGIC_CRITICAL_EFFECT
    }
}
