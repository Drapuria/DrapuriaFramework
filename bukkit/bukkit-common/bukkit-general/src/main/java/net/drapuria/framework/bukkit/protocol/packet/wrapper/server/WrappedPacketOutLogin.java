package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.MethodResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ConstructorWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.MethodWrapper;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.WorldType;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;

import javax.annotation.Nullable;

@AutowiredWrappedPacket(value = PacketType.Server.LOGIN, direction = PacketDirection.WRITE)
@Getter
@Setter
public class WrappedPacketOutLogin extends WrappedPacket implements SendableWrapper {

    private static Class<?> PACKET_CLASS, WORLD_TYPE_CLASS;
    private static Class<? extends Enum> ENUM_GAMEMODE_CLASS, ENUM_DIFFICULTY_CLASS;
    private static MethodWrapper NMS_WORLD_TYPE_GET_BY_NAME, NMS_WORLD_TYPE_NAME;
    private static ConstructorWrapper<?> PACKET_CONSTRUCTOR;

    private int playerId;
    private boolean hardcore;
    @Nullable private GameMode gameMode;
    private int dimension;
    private Difficulty difficulty;
    private int maxPlayers;
    private WorldType worldType;
    private boolean reducedDebugInfo;

    public WrappedPacketOutLogin(Object packet) {
        super(packet);
    }

    public WrappedPacketOutLogin() {
        super();
    }

    public WrappedPacketOutLogin(int playerId, boolean hardcore, GameMode gameMode, int dimension, Difficulty difficulty, int maxPlayers, WorldType worldType, boolean reducedDebugInfo) {
        super();
        this.playerId = playerId;
        this.hardcore = hardcore;
        this.gameMode = gameMode;
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.maxPlayers = maxPlayers;
        this.worldType = worldType;
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public static void init() {

        PACKET_CLASS = PacketTypeClasses.Server.LOGIN;

        try {
            ENUM_GAMEMODE_CLASS = Minecraft.getEnumGamemodeClass();

            ENUM_DIFFICULTY_CLASS = NMS_CLASS_RESOLVER.resolve("EnumDifficulty");
            WORLD_TYPE_CLASS = NMS_CLASS_RESOLVER.resolve("WorldType");

            MethodResolver methodResolver = new MethodResolver(WORLD_TYPE_CLASS);
            NMS_WORLD_TYPE_GET_BY_NAME = methodResolver.resolve(WORLD_TYPE_CLASS, 0, String.class);
            NMS_WORLD_TYPE_NAME = methodResolver.resolve(String.class, 0);

            PACKET_CONSTRUCTOR = new ConstructorWrapper<>(PACKET_CLASS.getDeclaredConstructor(
                    int.class,
                    ENUM_GAMEMODE_CLASS,
                    boolean.class,
                    int.class,
                    ENUM_DIFFICULTY_CLASS,
                    int.class,
                    WORLD_TYPE_CLASS,
                    boolean.class
            ));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

    }

    @Override
    protected void setup() {
        this.playerId = readInt(0);

        this.hardcore = readBoolean(0);

        this.gameMode = Minecraft.getGameModeConverter().getSpecific(readObject(0, ENUM_GAMEMODE_CLASS));

        this.dimension = readInt(1);

        Enum difficultyEnum = readObject(0, ENUM_DIFFICULTY_CLASS);
        if (difficultyEnum != null) {
            this.difficulty = Difficulty.valueOf(difficultyEnum.name());
        }

        Object worldType = readObject(0, WORLD_TYPE_CLASS);
        if (worldType != null) {
            this.worldType = WorldType.getByName((String) NMS_WORLD_TYPE_NAME.invoke(worldType));
        }

        this.maxPlayers = readInt(2);

        this.reducedDebugInfo = readBoolean(0);
    }

    @Override
    public Object asNMSPacket() {
        return PACKET_CONSTRUCTOR.newInstance(
                this.playerId,
                Minecraft.getGameModeConverter().getGeneric(this.gameMode),
                this.hardcore,
                this.dimension,
                Enum.valueOf(ENUM_DIFFICULTY_CLASS, this.difficulty.name()),
                this.maxPlayers,
                NMS_WORLD_TYPE_GET_BY_NAME.invoke(null, this.worldType.getName()),
                this.reducedDebugInfo
        );
    }
}
