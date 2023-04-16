package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@AutowiredWrappedPacket(value = PacketType.Server.UPDATE_HEALTH, direction = PacketDirection.WRITE)
@Setter
@Getter
public final class WrappedPacketOutUpdateHealth extends WrappedPacket implements SendableWrapper {

    private static Class<?> packetClass;
    private static Constructor<?> packetConstructor;

    private float health, foodSaturation;
    private int food;

    public WrappedPacketOutUpdateHealth(final Object packet) {
        super(packet);
    }

    /**
     * See https://wiki.vg/Protocol#Update_Health
     *
     * @param health 0 or less = dead, 20 = full HP
     * @param food 0–20
     * @param foodSaturation Seems to vary from 0.0 to 5.0 in integer increments
     */
    public WrappedPacketOutUpdateHealth(final float health, final int food, final float foodSaturation) {
        super();
        this.health = health;
        this.food = food;
        this.foodSaturation = foodSaturation;
    }

    public static void init() {
        packetClass = PacketTypeClasses.Server.UPDATE_HEALTH;

        try {
            packetConstructor = packetClass.getConstructor(float.class, int.class, float.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setup() {
        this.health = readFloat(0);
        this.foodSaturation = readFloat(1);
        this.food = new FieldResolver(packetClass).resolve(int.class, 0).get(packet);

    }

    @Override
    public Object asNMSPacket() {
        try {
            return packetConstructor.newInstance(health, food, foodSaturation);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
