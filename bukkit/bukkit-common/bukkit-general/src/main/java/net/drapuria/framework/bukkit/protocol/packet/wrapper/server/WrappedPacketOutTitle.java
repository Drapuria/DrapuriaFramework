package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

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
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ChatComponentWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.PacketWrapper;

import static com.mysql.jdbc.Util.readObject;

@AutowiredWrappedPacket(value = PacketType.Server.TITLE, direction = PacketDirection.WRITE)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class WrappedPacketOutTitle extends WrappedPacket implements SendableWrapper {

    public static final int DEFAULT_FADE_IN = 20;
    public static final int DEFAULT_STAY = 200;
    public static final int DEFAULT_FADE_OUT = 20;

    private Action action;
    private ChatComponentWrapper message;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    public WrappedPacketOutTitle(Object packet) {
        super(packet);
    }

    @Override
    protected void setup() {

        this.action = Minecraft.getTitleActionConverter().getSpecific(readObject(0, Minecraft.getEnumTitleActionClass()));
        this.message = readChatComponent(0);

        this.fadeIn = readInt(0);
        this.stay = readInt(1);
        this.fadeOut = readInt(2);

    }

    @Override
    public Object asNMSPacket() {
        PacketWrapper packetWrapper = new PacketWrapper(PacketTypeClasses.Server.TITLE);
        packetWrapper.setFieldByIndex(Minecraft.getEnumTitleActionClass(), 0, Minecraft.getTitleActionConverter().getGeneric(this.action));
        if (this.message != null) {
            packetWrapper.setFieldByIndex(Minecraft.getIChatBaseComponentClass(), 0, this.message.getHandle());
        }

        return packetWrapper.setFieldByIndex(int.class, 0, this.fadeIn)
                .setFieldByIndex(int.class, 1, this.stay)
                .setFieldByIndex(int.class, 2, this.fadeOut)
                .getPacket();
    }

    public static enum Action {
        TITLE,
        SUBTITLE,
        TIMES,
        CLEAR,
        RESET;
    }

}
