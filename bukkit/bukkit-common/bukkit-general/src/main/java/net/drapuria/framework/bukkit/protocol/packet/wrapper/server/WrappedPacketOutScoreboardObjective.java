package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.PacketWrapper;

import java.util.Map;

@AutowiredWrappedPacket(value = PacketType.Server.SCOREBOARD_OBJECTIVE, direction = PacketDirection.WRITE)
@Getter
@Setter
public class WrappedPacketOutScoreboardObjective extends WrappedPacket implements SendableWrapper {

    private String name = "";
    private String displayName = "";
    private HealthDisplayType healthDisplayType = HealthDisplayType.HEARTS;
    private Action action = Action.ADD;

    public WrappedPacketOutScoreboardObjective(Object packet) {
        super(packet);
    }

    public WrappedPacketOutScoreboardObjective(@NonNull String name, @NonNull String displayName, @NonNull HealthDisplayType healthDisplayType, @NonNull Action action) {
        super();
        this.name = name;
        this.displayName = displayName;
        this.healthDisplayType = healthDisplayType;
        this.action = action;
    }

    public WrappedPacketOutScoreboardObjective() {
        super();
    }

    @Override
    protected void setup() {

        this.name = readString(0);
        this.displayName = readString(1);
        this.healthDisplayType = Minecraft.getHealthDisplayTypeConverter().getSpecific(readObject(0, Minecraft.getHealthDisplayTypeClass()));
        this.action = Action.getById(readInt(0));

    }

    @Override
    public Object asNMSPacket() {
        return new PacketWrapper(PacketTypeClasses.Server.SCOREBOARD_OBJECTIVE)
                .setFieldByIndex(String.class, 0, this.name)
                .setFieldByIndex(String.class, 1, this.displayName)
                .setFieldByIndex(Minecraft.getHealthDisplayTypeClass(), 0, Minecraft.getHealthDisplayTypeConverter().getGeneric(this.healthDisplayType))
                .setFieldByIndex(int.class, 0, this.action.getId())
                .getPacket();
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
