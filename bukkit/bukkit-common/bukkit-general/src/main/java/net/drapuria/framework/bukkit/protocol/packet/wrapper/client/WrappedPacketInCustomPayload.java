package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import lombok.Getter;
import lombok.SneakyThrows;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.FieldWrapper;

import java.lang.reflect.Field;

@AutowiredWrappedPacket(value = PacketType.Client.CUSTOM_PAYLOAD, direction = PacketDirection.READ)
@Getter
public final class WrappedPacketInCustomPayload extends WrappedPacket {
    private static Class<?> packetClass, nmsMinecraftKey, nmsPacketDataSerializer;

    private static boolean strPresentInIndex0;
    private String data;
    private Object minecraftKey, dataSerializer;
    public WrappedPacketInCustomPayload(Object packet) {
        super(packet);
    }

    public static void init() {
        packetClass = PacketTypeClasses.Client.CUSTOM_PAYLOAD;
        if (packetClass == null) {
            return;
        }
        strPresentInIndex0 = new FieldResolver(packetClass)
            .resolveSilent(String.class, 0)
            .exists();
        try {
            nmsPacketDataSerializer = NMS_CLASS_RESOLVER.resolve("PacketDataSerializer");
        } catch (ClassNotFoundException e) {
            if (Minecraft.VERSION != null && Minecraft.VERSION.olderThan(Minecraft.Version.v1_19_R1)) {
                e.printStackTrace();
            }
        }
        try {
            //Only on 1.13+
            nmsMinecraftKey = NMS_CLASS_RESOLVER.resolve("MinecraftKey");
        } catch (ClassNotFoundException e) {
            //Its okay, this means they are on versions 1.7.10 - 1.12.2
        }
    }

    @SneakyThrows
    @Override
    public void setup() {
        if (!strPresentInIndex0) {
            this.minecraftKey = readObject(0, nmsMinecraftKey);
            this.dataSerializer = readObject(0, nmsPacketDataSerializer);

        } else {
            this.data = readString(0);

            FieldWrapper<?> field = this.packet.getFieldByIndex(nmsPacketDataSerializer, 0);
            if (field != null) {
                this.dataSerializer = field.get(this.packet.getPacket());
            }
        }
    }

}
