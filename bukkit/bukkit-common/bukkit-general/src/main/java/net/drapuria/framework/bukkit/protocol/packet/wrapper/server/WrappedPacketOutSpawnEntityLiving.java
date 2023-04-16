package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import com.comphenix.protocol.wrappers.collection.ConvertedList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.minecraft.DataWatcher;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.DataWatcherWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.PacketWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.WatchableObjectWrapper;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@AutowiredWrappedPacket(value = PacketType.Server.SPAWN_ENTITY_LIVING, direction = PacketDirection.WRITE)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class WrappedPacketOutSpawnEntityLiving extends WrappedPacket implements SendableWrapper {
    public WrappedPacketOutSpawnEntityLiving(Object packet) {
        super(packet);
    }

    private int entityId, entityTypeId;
    private int locX, locY, locZ;

    private float yaw, pitch, headPitch;

    private double velX, velY, velZ;

    private DataWatcherWrapper dataWatcher;
    private ConvertedList<Object, WatchableObjectWrapper> watchableObjects;

    @Override
    protected void setup() {
        this.entityId = readInt(0);
        this.entityTypeId = readInt(1);

        this.locX = readInt(2) / 32;
        this.locY = readInt(3) / 32;
        this.locZ = readInt(4) / 32;

        this.yaw = readByte(0) / 256.0F * 360.0F;
        this.pitch = readByte(1) / 256.0F * 360.0F;
        this.headPitch = readByte(2) / 256.0F * 360.0F;

        this.velX = readInt(5) / 8000.0D;
        this.velY = readInt(6) / 8000.0D;
        this.velZ = readInt(7) / 8000.0D;

        this.dataWatcher = new DataWatcherWrapper(readObject(0, DataWatcher.TYPE));
        this.watchableObjects = new ConvertedList<Object, WatchableObjectWrapper>(readList(0)) {
            @Override
            protected WatchableObjectWrapper toOuter(Object o) {
                return WatchableObjectWrapper.getConverter().getSpecific(o);
            }

            @Override
            protected Object toInner(WatchableObjectWrapper watchableObjectWrapper) {
                return WatchableObjectWrapper.getConverter().getGeneric(watchableObjectWrapper);
            }
        };
    }

    @Override
    public Object asNMSPacket() {
        PacketWrapper packetWrapper = new PacketWrapper(PacketTypeClasses.Server.SPAWN_ENTITY_LIVING)
                .setFieldByIndex(int.class, 0, this.entityId)
                .setFieldByIndex(int.class, 1, this.entityTypeId)
                .setFieldByIndex(int.class, 2, (int) Math.floor(this.locX * 32.0D))
                .setFieldByIndex(int.class, 3, (int) Math.floor(this.locY * 32.0D))
                .setFieldByIndex(int.class, 4, (int) Math.floor(this.locZ * 32.0D))
                .setFieldByIndex(byte.class, 0, (byte) ((int)(this.yaw * 256.0F / 360.0F)))
                .setFieldByIndex(byte.class, 1, (byte) ((int)(this.pitch * 256.0F / 360.0F)))
                .setFieldByIndex(byte.class, 2, (byte) ((int)(this.headPitch * 256.0F / 360.0F)))
                .setFieldByIndex(int.class, 5, (int) (this.velX * 8000.0D))
                .setFieldByIndex(int.class, 6, (int) (this.velY * 8000.0D))
                .setFieldByIndex(int.class, 7, (int) (this.velZ * 8000.0D))
                .setFieldByIndex(DataWatcher.TYPE, 0, this.dataWatcher.getDataWatcherObject());
        if (this.watchableObjects != null) {
            packetWrapper.setFieldByIndex(List.class, 0, this.watchableObjects.stream().map(watchableObjectWrapper -> WatchableObjectWrapper.getConverter().getGeneric(watchableObjectWrapper)).collect(Collectors.toList()));
        }

        return packetWrapper.getPacket();
    }

}
