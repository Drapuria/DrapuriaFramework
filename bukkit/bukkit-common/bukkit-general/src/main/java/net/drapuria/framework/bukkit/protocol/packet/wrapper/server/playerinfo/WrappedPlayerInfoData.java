package net.drapuria.framework.bukkit.protocol.packet.wrapper.server.playerinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.ConstructorResolver;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.NMSClassResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ChatComponentWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ConstructorWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.GameProfileWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ObjectWrapper;
import net.drapuria.framework.util.EquivalentConverter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class WrappedPlayerInfoData {

    private static Class<?> PLAYER_INFO_DATA_CLASS;

    public static boolean isPlayerInfoDataExists() {
        return PLAYER_INFO_DATA_CLASS != null;
    }

    private static ConstructorWrapper<?> CONSTRUCTOR;
    private static EquivalentConverter<WrappedPlayerInfoData> CONVERTER;

    static {
        NMSClassResolver classResolver = new NMSClassResolver();
        try {
            Class<?> playerInfoPacketClass = classResolver.resolve("PacketPlayOutPlayerInfo");

            try {
                PLAYER_INFO_DATA_CLASS = classResolver.resolve("PlayerInfoData");
            } catch (ClassNotFoundException ex) {
                try {
                    PLAYER_INFO_DATA_CLASS = classResolver.resolveSubClass(playerInfoPacketClass, "PlayerInfoData");
                } catch (ClassNotFoundException ex2) {
                    // 1.7
                }
            }
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private int latency;

    @Nullable
    private GameMode gameMode;

    private GameProfileWrapper gameProfile;
    private ChatComponentWrapper chatComponent;

    @SneakyThrows
    public static WrappedPlayerInfoData from(Player player) {

        GameMode gameMode = player.getGameMode();
        GameProfileWrapper gameProfile = GameProfileWrapper.fromPlayer(player);
        int ping = Minecraft.getPing(player);
        ChatComponentWrapper chatComponent = ChatComponentWrapper.fromText(player.getPlayerListName());

        return new WrappedPlayerInfoData(ping, gameMode, gameProfile, chatComponent);

    }

    public static EquivalentConverter<WrappedPlayerInfoData> getConverter() {
        if (CONVERTER != null) {
            return CONVERTER;
        }
        return CONVERTER = new EquivalentConverter<WrappedPlayerInfoData>() {
            @Override
            public Object getGeneric(WrappedPlayerInfoData specific) {
                if (PLAYER_INFO_DATA_CLASS == null || CONSTRUCTOR == null) {
                    NMSClassResolver classResolver = new NMSClassResolver();

                    try {
                        Class<?> playerInfoPacketClass = classResolver.resolve("PacketPlayOutPlayerInfo");

                        CONSTRUCTOR = new ConstructorResolver(PLAYER_INFO_DATA_CLASS)
                                .resolveMatches(new Class[] {
                                        playerInfoPacketClass,
                                        GameProfileWrapper.IMPLEMENTATION.getGameProfileClass(),
                                        int.class,
                                        Minecraft.getEnumGamemodeClass(),
                                        Minecraft.getIChatBaseComponentClass()
                                },
                                        new Class[] {
                                                GameProfileWrapper.IMPLEMENTATION.getGameProfileClass(),
                                                int.class,
                                                Minecraft.getEnumGamemodeClass(),
                                                Minecraft.getIChatBaseComponentClass()
                                        });
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }

                try {

                    return CONSTRUCTOR.resolve(
                            new Object[] {
                                    null,
                                    specific.gameProfile.getHandle(),
                                    specific.latency,
                                    Minecraft.getGameModeConverter().getGeneric(specific.getGameMode()),
                                    specific.chatComponent != null ? specific.chatComponent.getHandle() : null
                            },
                            new Object[] {
                                    specific.gameProfile.getHandle(),
                                    specific.latency,
                                    Minecraft.getGameModeConverter().getGeneric(specific.getGameMode()),
                                    specific.chatComponent != null ? specific.chatComponent.getHandle() : null
                            }
                    );

                } catch (Throwable throwable) {
                    throw new RuntimeException("Failed to construct NMS PlayerInfoData", throwable);
                }
            }

            @Override
            public WrappedPlayerInfoData getSpecific(Object generic) {
                ObjectWrapper objectWrapper = new ObjectWrapper(generic);

                GameProfileWrapper gameProfile = (GameProfileWrapper) objectWrapper.getFieldByFirstType(GameProfileWrapper.IMPLEMENTATION.getGameProfileClass());
                int latency = objectWrapper.getFieldByFirstType(int.class);

                GameMode gameMode = Minecraft.getGameModeConverter().getSpecific(objectWrapper.getFieldByFirstType(Minecraft.getEnumGamemodeClass()));

                ChatComponentWrapper chatComponent = Minecraft.getChatComponentConverter().getSpecific(objectWrapper.getFieldByFirstType(ChatComponentWrapper.GENERIC_TYPE));

                return new WrappedPlayerInfoData(latency, gameMode, gameProfile, chatComponent);
            }

            @Override
            public Class<WrappedPlayerInfoData> getSpecificType() {
                return WrappedPlayerInfoData.class;
            }
        };
    }

}
