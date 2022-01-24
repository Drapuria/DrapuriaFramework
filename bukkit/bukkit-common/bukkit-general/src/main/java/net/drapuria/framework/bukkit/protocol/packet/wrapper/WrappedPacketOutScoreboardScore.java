/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.packet.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.protocollib.ProtocolLibService;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class WrappedPacketOutScoreboardScore implements SendableWrapper {

    private static boolean ACTION_IS_ENUM;
    private static final Class<? extends Enum> enumScoreboardActionClass = Minecraft.getEnumScoreboardActionClass();
    private static final Class<? extends Enum> genericScoreboardActionClass = Minecraft.getScoreboardActionConverter().getGenericType();
    private static final Map<ScoreboardAction, Object> convertedActions = new HashMap<>();

    static {
        try {
            Minecraft.getEnumScoreboardActionClass();
            ACTION_IS_ENUM = true;
        } catch (Throwable ignored) {
            ACTION_IS_ENUM = false;
        }
    }

    private String entry;
    private String objective;
    private int score;
    private ScoreboardAction action;

    @SuppressWarnings("unchecked")
    @Override
    public PacketContainer asProtocolLibPacketContainer() {
        final PacketContainer packetContainer = ProtocolLibService.getService.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
        packetContainer.getStrings().write(0, this.entry);
        packetContainer.getStrings().write(1, this.objective);
        packetContainer.getIntegers().write(0, this.score);
        if (ACTION_IS_ENUM) {
            Object convertedEnum;
            if (convertedActions.containsKey(action)) {
                convertedEnum = convertedActions.get(action);
            } else {
                convertedEnum = Minecraft.getScoreboardActionConverter().getGeneric(this.action);
                convertedActions.put(action, convertedEnum);
            }
            packetContainer.getEnumModifier(enumScoreboardActionClass,
                    genericScoreboardActionClass).write(0,
                    convertedEnum);
        } else {
            packetContainer.getIntegers().write(1, this.action.id);
        }
        return packetContainer;
    }

    @Getter
    public static enum ScoreboardAction {
        CHANGE(0),
        REMOVE(1);

        private final int id;

        ScoreboardAction(int id) {
            this.id = id;
        }

        public static ScoreboardAction getById(int id) {
            for (ScoreboardAction action : values()) {
                if (action.id == id) {
                    return action;
                }
            }
            return null;
        }
    }
}
