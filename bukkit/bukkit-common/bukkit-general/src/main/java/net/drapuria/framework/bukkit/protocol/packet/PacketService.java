package net.drapuria.framework.bukkit.protocol.packet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.beans.annotation.Autowired;
import net.drapuria.framework.beans.annotation.PostDestroy;
import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.protocol.packet.netty.INettyInjection;
import net.drapuria.framework.bukkit.protocol.packet.netty.NettyInjection1_8;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.PacketContainer;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.imanity.framework.reflect.ReflectLookup;

import java.lang.reflect.Method;
import java.util.Collections;

@Service(name = "Packet")
public class PacketService {

    public static final String CHANNEL_HANDLER = "Drapuria_" + "ChannelHandler";

    @Autowired
    private static PacketService INSTANCE;

    @Autowired
    private BeanContext beanContext;

    public static void send(Player player, SendableWrapper sendableWrapper) {
        PacketService.INSTANCE.sendPacket(player, sendableWrapper);
    }

    private final Multimap<Class<?>, PacketListener> registeredPacketListeners = HashMultimap.create();

    @Getter
    private INettyInjection nettyInjection;

    @PostInitialize
    public void init() {

        try {

            Class.forName("io.netty.channel.Channel");
            nettyInjection = new NettyInjection1_8();

        } catch (ClassNotFoundException ex) {

//            nettyInjection = new NettyInjection1_7();

        }

        PacketTypeClasses.load();
        WrappedPacket.init();

        try {
            nettyInjection.registerChannels();
        } catch (Throwable throwable) {
            Drapuria.PLUGIN.getLogger().info("Late Bind was enabled, late inject channels.");
            DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> {
                try {
                    nettyInjection.registerChannels();
                } catch (Throwable throwable1) {
                    throw new RuntimeException(throwable1);
                }
            }, 0L);
        }

        DrapuriaCommon.BEAN_CONTEXT.injectBeans(nettyInjection);
        Bukkit.getOnlinePlayers().forEach(this::inject);

        try {

            this.loadWrappers();

        } catch (Throwable throwable) {
            throw new RuntimeException("Something wrong while loading wrapped packets", throwable);
        }
    }

    private void loadWrappers() throws Throwable {
        ImmutableMap.Builder<Byte, Class<? extends WrappedPacket>> readBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<Byte, Class<? extends WrappedPacket>> writeBuilder = ImmutableMap.builder();

        ReflectLookup reflectLookup = new ReflectLookup(
                Collections.singleton(PacketService.class.getClassLoader()),
                Collections.singleton("net.drapuria.framework")
        );

        for (java.lang.Class<?> originalType : reflectLookup.findAnnotatedClasses(AutowiredWrappedPacket.class)) {

            if (!WrappedPacket.class.isAssignableFrom(originalType)) {
                throw new IllegalArgumentException("The type " + originalType.getName() + " does not extend WrappedPacket!");
            }

            Class<? extends WrappedPacket> type = (Class<? extends WrappedPacket>) originalType;

            try {
                AutowiredWrappedPacket annotation = type.getAnnotation(AutowiredWrappedPacket.class);

                Method method = type.getDeclaredMethod("init");
                method.invoke(null);

                switch (annotation.direction()) {
                    case READ:
                        readBuilder.put(annotation.value(), type);
                        break;
                    case WRITE:
                        writeBuilder.put(annotation.value(), type);
                        break;
                }
            } catch (NoSuchMethodException ex) {
                // Ignores
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        PacketDirection.READ.register(readBuilder.build());
        PacketDirection.WRITE.register(writeBuilder.build());
    }

    @PostDestroy
    public void stop() {
        this.nettyInjection.unregisterChannels();
    }

    public void registerPacketListener(PacketListener packetListener) {
        for (Class<?> type : packetListener.type()) {
            if (type == null) {
                throw new UnsupportedOperationException("There is one packet doesn't exists in current version!");
            }

            this.registeredPacketListeners.put(type, packetListener);
        }
    }

    public void inject(Player player) {
        this.nettyInjection.inject(player);
    }

    public void eject(Player player) {
        this.nettyInjection.eject(player);
    }

    public Object read(Player player, Object packet) {
        Class<?> type = packet.getClass();

        if (!this.registeredPacketListeners.containsKey(type)) {
            return packet;
        }

        WrappedPacket wrappedPacket = PacketDirection.READ.getWrappedFromNMS(player, PacketType.Client.getIdByType(type), packet);

        PacketDto packetDto = new PacketDto(wrappedPacket);

        boolean cancelled = false;
        for (PacketListener packetListener : this.registeredPacketListeners.get(type)) {
            if (!packetListener.read(player, packetDto)) {
                cancelled = true;
            }
        }

        return cancelled ? null : packet;
    }

    public Object write(Player player, Object packet) {
        Class<?> type = packet.getClass();

        if (!this.registeredPacketListeners.containsKey(type)) {
            return packet;
        }

        WrappedPacket wrappedPacket = PacketDirection.WRITE.getWrappedFromNMS(player, PacketType.Server.getIdByType(type), packet);

        PacketDto packetDto = new PacketDto(wrappedPacket);

        boolean cancelled = false;
        for (PacketListener packetListener : this.registeredPacketListeners.get(type)) {
            if (!packetListener.write(player, packetDto)) {
                cancelled = true;
            }
        }

        return cancelled ? null : packetDto.isRefresh() ? ((SendableWrapper) wrappedPacket).asNMSPacket() : packet;
    }

    public void sendPacket(Player player, SendableWrapper packet) {
        PacketContainer packetContainer = packet.asPacketContainer();
        Minecraft.sendPacket(player, packetContainer.getMainPacket());

        for (Object extra : packetContainer.getExtraPackets()) {
            Minecraft.sendPacket(player, extra);
        }
    }
}
