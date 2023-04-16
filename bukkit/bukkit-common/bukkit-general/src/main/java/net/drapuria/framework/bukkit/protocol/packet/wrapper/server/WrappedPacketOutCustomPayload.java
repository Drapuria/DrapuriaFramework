package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;


import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.protocol.packet.PacketService;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.reflection.resolver.MethodResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class WrappedPacketOutCustomPayload extends WrappedPacket implements SendableWrapper {

    private static Class<?> packetClass;
    private static Constructor<?> constructor;
    private static Constructor<?> packetDataSerializerConstructor;
    private static Constructor<?> minecraftKeyConstructor;
    private static Class<?> byteBufClass;
    private static Class<?> unpooledClass;
    private static Class<?> packetDataSerializerClass;
    private static Class<?> minecraftKeyClass;

    private static byte constructorMode = 1;

    public static void init() {
        packetClass = PacketTypeClasses.Server.CUSTOM_PAYLOAD;
        packetDataSerializerClass = NMS_CLASS_RESOLVER.resolveSilent("PacketDataSerializer");
        minecraftKeyClass = NMS_CLASS_RESOLVER.resolveSilent("MinecraftKey");

        try {
            unpooledClass = NMS_CLASS_RESOLVER.resolve("buffer.Unpooled");
            byteBufClass = NMS_CLASS_RESOLVER.resolve("buffer.ByteBuf");
        } catch (ClassNotFoundException e) {

        }
        try {
            packetDataSerializerConstructor = packetDataSerializerClass.getConstructor(NETTY_CLASS_RESOLVER.resolve("ByteBuf"));
        } catch (NullPointerException e) {
            //Nothing
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            //also nothing
        }

        try {
            minecraftKeyConstructor = minecraftKeyClass.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            //Nothing
        }

        //Constructors:

        //String, byte[]

        //String, PacketDataSerializer

        //MinecraftKey, PacketDataSerializer
        try {
            //1.7 constructor
            constructor = packetClass.getConstructor(String.class, byte[].class);
            constructorMode = 0;
        } catch (NoSuchMethodException e) {
            //That's fine, just a newer version
            try {
                constructor = packetClass.getConstructor(String.class, packetDataSerializerClass);
                constructorMode = 1;
            } catch (NoSuchMethodException e2) {
                //That's fine, just an even newer version
                try {
                    constructor = packetClass.getConstructor(minecraftKeyClass, packetDataSerializerClass);
                    constructorMode = 2;
                } catch (NoSuchMethodException e3) {
                    throw new IllegalStateException("PacketEvents is unable to resolve the PacketPlayOutCustomPayload constructor.");
                }
            }
        }
    }

    private String tag;
    private byte[] data;

    public WrappedPacketOutCustomPayload(String tag, byte[] data) {
        this.tag = tag;
        this.data = data;
    }


    public WrappedPacketOutCustomPayload(Object packet) {
        super(packet);
    }

    @Override
    protected void setup() {

        switch (constructorMode) {
            case 0:
                this.tag = this.readString(0);
                this.data = this.readObject(0,  byte[].class);
                break;
            default:
                this.tag = this.readString(0);
                Object byteBuf = this.readObject(0, packetDataSerializerClass);

                this.data = DrapuriaCommon.getBean(PacketService.class).getNettyInjection().readBytes(byteBuf);
                break;
        }

    }

    public String getTag() {
        return tag;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public Object asNMSPacket() {
        Object byteBufObject = new MethodResolver(unpooledClass)
                .resolveWrapper("copiedBuffer")
                .invoke(null, data);

        switch (constructorMode) {
            case 0:
                try {
                    return constructor.newInstance(tag, data);
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    Object dataSerializer = packetDataSerializerConstructor.newInstance(byteBufObject);
                    return constructor.newInstance(tag, dataSerializer);
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case 2:

                try {
                    Object minecraftKey = minecraftKeyConstructor.newInstance(tag);
                    Object dataSerializer = packetDataSerializerConstructor.newInstance(byteBufObject);
                    return constructor.newInstance(minecraftKey, dataSerializer);
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }
}
