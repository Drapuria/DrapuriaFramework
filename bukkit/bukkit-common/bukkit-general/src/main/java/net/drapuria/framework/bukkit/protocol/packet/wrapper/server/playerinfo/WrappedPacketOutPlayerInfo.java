package net.drapuria.framework.bukkit.protocol.packet.wrapper.server.playerinfo;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.PacketContainer;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.minecraft.MinecraftVersion;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ChatComponentWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.GameProfileWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.PacketWrapper;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@AutowiredWrappedPacket(value = PacketType.Server.PLAYER_INFO, direction = PacketDirection.WRITE)
@Getter
@Setter
public class WrappedPacketOutPlayerInfo extends WrappedPacket implements SendableWrapper {

    private static Class<?> PACKET_CLASS;

    public static void init() {
        try {
            PACKET_CLASS = NMS_CLASS_RESOLVER.resolve("PacketPlayOutPlayerInfo");
        } catch (Throwable throwable) {

            // PACKET GOT REMOVED, WE DO NOT HAVE TO HANDLE THE EXCEPTION
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

    @Override
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

    @Override
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
