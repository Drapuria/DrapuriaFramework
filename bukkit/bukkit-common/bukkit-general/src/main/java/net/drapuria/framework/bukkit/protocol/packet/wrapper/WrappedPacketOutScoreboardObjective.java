/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.packet.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.ProtocolService;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class WrappedPacketOutScoreboardObjective implements SendableWrapper {

    private String name = "";
    private String displayName = "";
    private HealthDisplayType healthDisplayType = HealthDisplayType.HEARTS;
    private Action action = Action.ADD;

    private static boolean HAS_CHAT_FORMAT;

    private static Map<HealthDisplayType, Object> convertedHealthDisplayType = new HashMap<>();
    private static final Class<? extends Enum> healthDisplayTypeClass = Minecraft.getHealthDisplayTypeClass();
    private static final Class<? extends Enum> healthDisplayConverterGenericType  = Minecraft.getHealthDisplayTypeConverter().getGenericType();
    static {
        HAS_CHAT_FORMAT = Minecraft.MINECRAFT_VERSION.version() >= 11601;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PacketContainer asProtocolLibPacketContainer() {
        final PacketContainer packetContainer = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packetContainer.getIntegers().write(0, action.id);
        packetContainer.getStrings().write(0, name);
        if (HAS_CHAT_FORMAT) {
            packetContainer.getChatComponents().write(0, WrappedChatComponent.fromText(this.displayName));
        } else {
            packetContainer.getStrings().write(1, displayName);
            Object convertedHealthDisplayType;
            if (WrappedPacketOutScoreboardObjective.convertedHealthDisplayType.containsKey(this.healthDisplayType)) {
                convertedHealthDisplayType = WrappedPacketOutScoreboardObjective
                        .convertedHealthDisplayType.get(this.healthDisplayType);
            } else {
                convertedHealthDisplayType = Minecraft.getHealthDisplayTypeConverter().getGeneric(healthDisplayType);
                WrappedPacketOutScoreboardObjective.convertedHealthDisplayType
                        .put(this.healthDisplayType, convertedHealthDisplayType);
            }
            packetContainer.getEnumModifier(healthDisplayTypeClass, healthDisplayConverterGenericType).write(0,
                    Minecraft.getHealthDisplayTypeConverter().getGeneric(healthDisplayType));
        }
        return packetContainer;
    }

    @Getter
    public enum Action {

        ADD(0),
        REMOVE(1),
        CHANGED(2);

        private final int id;

        Action(int id) {
            this.id = id;
        }

        public static Action getById(int id) {
            for (Action action : values()) {
                if (action.id == id) {
                    return action;
                }
            }

            return null;
        }
    }

    public enum HealthDisplayType {
        INTEGER("integer"),
        HEARTS("hearts");

        private static final Map<String, HealthDisplayType> NAME_TO_TYPE_MAP;
        private final String name;

        HealthDisplayType(String var3) {
            this.name = var3;
        }

        public String getName() {
            return this.name;
        }

        public static HealthDisplayType getByName(String name) {
            return NAME_TO_TYPE_MAP.getOrDefault(name, INTEGER);
        }

        static {

            ImmutableMap.Builder<String, HealthDisplayType> builder = ImmutableMap.builder();

            for (HealthDisplayType displayType : values()) {
                builder.put(displayType.getName(), displayType);
            }

            NAME_TO_TYPE_MAP = builder.build();

        }
    }
}
