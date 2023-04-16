package net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation;


import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AutowiredWrappedPacket {

    byte value();

    PacketDirection direction();

}
