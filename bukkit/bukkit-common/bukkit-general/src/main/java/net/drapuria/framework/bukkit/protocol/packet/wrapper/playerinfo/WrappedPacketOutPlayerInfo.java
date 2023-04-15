package net.drapuria.framework.bukkit.protocol.packet.wrapper.playerinfo;

import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.NMSClassResolver;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.OBCClassResolver;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

//@AutowiredWrappedPacket(value = PacketType.Server.PLAYER_INFO, direction = PacketDirection.WRITE)
@Getter
@Setter
public class WrappedPacketOutPlayerInfo implements SendableWrapper {

    private static Class<?> PACKET_CLASS;
    public static final NMSClassResolver NMS_CLASS_RESOLVER = new NMSClassResolver();
    public static final OBCClassResolver CRAFT_CLASS_RESOLVER = new OBCClassResolver();
    public static final NettyClassResolver NETTY_CLASS_RESOLVER = new NettyClassResolver();

    public static void init() {
        try {
            PACKET_CLASS = NMS_CLASS_RESOLVER.resolve("PacketPlayOutPlayerInfo");
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public WrappedPacketOutPlayerInfo() {
        super();
    }

    private PlayerInfoAction action;
    private List<WrappedPlayerInfoData> playerInfoData = new ArrayList<>();

    public WrappedPacketOutPlayerInfo(Object packet) {
        super(packet);
    }

    public WrappedPacketOutPlayerInfo(Player player, Object packet) {
        super(player, packet);
    }

    public WrappedPacketOutPlayerInfo(PlayerInfoAction action, Player player) {
        this.action = action;
        this.playerInfoData.add(WrappedPlayerInfoData.from(player));
    }

    public WrappedPacketOutPlayerInfo(PlayerInfoAction action, WrappedPlayerInfoData... playerInfoData) {
        this.action = action;
        this.playerInfoData.addAll(Arrays.asList(playerInfoData));
    }

    public WrappedPacketOutPlayerInfo(PlayerInfoAction action, Collection<WrappedPlayerInfoData> playerInfoData) {
        this.action = action;
        this.playerInfoData.addAll(playerInfoData);
    }

    protected void setup() {

        if (WrappedPlayerInfoData.isPlayerInfoDataExists()) { // 1.8

            action = PlayerInfoAction.getConverter().getSpecific(readObject(0, PlayerInfoAction.getGenericType()));
            for (Object genericInfoData : readObject(0, List.class)) {
                playerInfoData.add(WrappedPlayerInfoData.getConverter().getSpecific(genericInfoData));
            }

        } else { // 1.7

            this.action = PlayerInfoAction.getById(readInt(0));

            GameProfileWrapper gameProfile = this.readGameProfile(0);
            int gamemode = this.readInt(1);
            int ping = this.readInt(2);
            String username = this.readString(0);

            this.playerInfoData.add(new WrappedPlayerInfoData(ping, GameMode.getByValue(gamemode), gameProfile, ChatComponentWrapper.fromText(username)));

        }

    }

    public PacketContainer asPacketContainer() {

        if (WrappedPlayerInfoData.isPlayerInfoDataExists()) { // 1.8
            try {
                PacketWrapper packetWrapper = new PacketWrapper(PACKET_CLASS.newInstance());

                packetWrapper.setPacketValueByType(PlayerInfoAction.getGenericType(), PlayerInfoAction.getConverter().getGeneric(this.action));
                List genericInfoDataList = packetWrapper.getPacketValueByIndex(List.class, 0);

                for (WrappedPlayerInfoData playerInfoData : this.playerInfoData) {
                    genericInfoDataList.add(WrappedPlayerInfoData.getConverter().getGeneric(playerInfoData));
                }

                return PacketContainer.of(packetWrapper.getPacket());
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        } else {
            try {

                Object mainPacket = null;
                List<Object> extraPackets = null;

                for (WrappedPlayerInfoData playerInfoData : this.playerInfoData) {
                    PacketWrapper packetWrapper = new PacketWrapper(PACKET_CLASS.newInstance());

                    packetWrapper.setFieldByIndex(int.class, 0, this.action.getId());
                    packetWrapper.setFieldByIndex(GameProfileWrapper.IMPLEMENTATION.getGameProfileClass(), 0, playerInfoData.getGameProfile().getHandle());
                    packetWrapper.setFieldByIndex(int.class, 1, playerInfoData.getGameMode() != null ? playerInfoData.getGameMode().getValue() : -1);
                    packetWrapper.setFieldByIndex(int.class, 2, playerInfoData.getLatency());
                    packetWrapper.setFieldByIndex(String.class, 0, playerInfoData.getChatComponent().toLegacyText());

                    if (mainPacket == null) {
                        mainPacket = packetWrapper.getPacket();
                    } else {
                        if (extraPackets == null) {
                            extraPackets = new ArrayList<>();
                        }

                        extraPackets.add(packetWrapper.getPacket());
                    }
                }

                if (mainPacket == null) {
                    return PacketContainer.empty();
                }

                return PacketContainer.builder()
                        .mainPacket(mainPacket)
                        .extraPackets(extraPackets)
                        .build();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

    }
}
