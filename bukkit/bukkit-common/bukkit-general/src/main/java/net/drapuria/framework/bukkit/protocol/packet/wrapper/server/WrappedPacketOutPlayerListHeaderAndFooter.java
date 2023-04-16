package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ChatComponentWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.PacketWrapper;

@AutowiredWrappedPacket(value = PacketType.Server.PLAYER_LIST_HEADER_FOOTER, direction = PacketDirection.WRITE)
@Getter
@Setter
public class WrappedPacketOutPlayerListHeaderAndFooter extends WrappedPacket implements SendableWrapper {

    private ChatComponentWrapper header;
    private ChatComponentWrapper footer;

    public WrappedPacketOutPlayerListHeaderAndFooter(Object packet) {
        super(packet);
    }

    public WrappedPacketOutPlayerListHeaderAndFooter(ChatComponentWrapper header, ChatComponentWrapper footer) {
        this.header = header;
        this.footer = footer;
    }

    @Override
    protected void setup() {
        this.header = readChatComponent(0);
        this.footer = readChatComponent(1);
    }

    @Override
    public Object asNMSPacket() {
        try {

            Object packet = PacketTypeClasses.Server.PLAYER_LIST_HEADER_FOOTER.newInstance();
            PacketWrapper objectWrapper = new PacketWrapper(packet);

            objectWrapper.setFieldByIndex(Minecraft.getIChatBaseComponentClass(), 0, Minecraft.getChatComponentConverter().getGeneric(this.header));
            objectWrapper.setFieldByIndex(Minecraft.getIChatBaseComponentClass(), 1, Minecraft.getChatComponentConverter().getGeneric(this.footer));

            return packet;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
