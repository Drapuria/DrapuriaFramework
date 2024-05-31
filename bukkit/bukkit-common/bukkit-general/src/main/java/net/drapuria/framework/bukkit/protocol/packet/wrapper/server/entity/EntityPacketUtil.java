package net.drapuria.framework.bukkit.protocol.packet.wrapper.server.entity;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;

import java.lang.reflect.Field;

public class EntityPacketUtil {

    @Getter private static byte mode = 0; //byte = 0, int = 1, short = 2
    @Getter private static double dXYZDivisor = 0.0;

    public static void init() {
        Class<?> packetClass = PacketTypeClasses.Server.ENTITY;

        try {
            FieldResolver fieldResolver = new FieldResolver(packetClass);
            Field dxField = fieldResolver.resolveIndex(1);
            assert dxField != null;

            boolean resolved = false;

            try {
                if (dxField.equals(fieldResolver.resolve(byte.class, 0).getField())) {
                    mode = 0;
                    resolved = true;
                }
            } catch (Exception e) {
                // Ignore
            }

            if (!resolved) {
                try {
                    if (fieldResolver.resolveSilent(int.class, 1) != null && dxField.equals(fieldResolver.resolve(int.class, 1).getField())) {
                        mode = 1;
                        resolved = true;
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }

            if (!resolved) {
                try {
                    if (dxField.equals(fieldResolver.resolve(short.class, 0).getField())) {
                        mode = 2;
                        resolved = true;
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        if (mode == 0) {
            dXYZDivisor = 32.0;
        } else {
            dXYZDivisor = 4096.0;
        }

    }
}