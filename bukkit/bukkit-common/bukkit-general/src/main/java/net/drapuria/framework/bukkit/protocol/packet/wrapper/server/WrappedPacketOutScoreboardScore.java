package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.PacketWrapper;
import org.bukkit.entity.Player;

@AutowiredWrappedPacket(value = PacketType.Server.SCOREBOARD_SCORE, direction = PacketDirection.WRITE)
@Getter
@Setter
public class WrappedPacketOutScoreboardScore extends WrappedPacket implements SendableWrapper {

    private static boolean ACTION_IS_ENUM;

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

    public WrappedPacketOutScoreboardScore(Player player, Object packet) {
        super(player, packet);
    }

    public WrappedPacketOutScoreboardScore(Object packet) {
        super(packet);
    }

    public WrappedPacketOutScoreboardScore(String entry, String objective, int score, ScoreboardAction action) {
        super();
        this.entry = entry;
        this.objective = objective;
        this.score = score;
        this.action = action;
    }

    @Override
    protected void setup() {
        this.entry = readString(0);
        this.objective = readString(1);
        this.score = readInt(0);

        if (ACTION_IS_ENUM) {
            this.action = Minecraft.getScoreboardActionConverter().getSpecific(readObject(0, Minecraft.getEnumScoreboardActionClass()));
        } else {
            this.action = ScoreboardAction.getById(readInt(1));
        }
    }

    @Override
    public Object asNMSPacket() {
        PacketWrapper packet = PacketWrapper.createByPacketName("PacketPlayOutScoreboardScore");

        packet.setFieldByIndex(String.class, 0, this.entry);
        packet.setFieldByIndex(String.class, 1, this.objective);
        packet.setFieldByIndex(int.class, 0, this.score);

        if (ACTION_IS_ENUM) {
            packet.setFieldByIndex(Minecraft.getEnumScoreboardActionClass(), 0, Minecraft.getScoreboardActionConverter().getGeneric(this.action));
        } else {
            packet.setFieldByIndex(int.class, 1, this.action.getId());
        }

        return packet.getPacket();
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