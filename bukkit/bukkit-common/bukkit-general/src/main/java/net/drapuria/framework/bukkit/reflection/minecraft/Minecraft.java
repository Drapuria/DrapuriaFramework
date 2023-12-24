package net.drapuria.framework.bukkit.reflection.minecraft;

import lombok.SneakyThrows;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.annotation.ProviderTestImpl;
import net.drapuria.framework.bukkit.impl.server.ServerImplementation;
import net.drapuria.framework.bukkit.impl.test.ImplementationFactory;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutScoreboardObjective;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutScoreboardScore;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutTitle;
import net.drapuria.framework.bukkit.reflection.Reflection;
import net.drapuria.framework.bukkit.reflection.annotation.ProtocolImpl;
import net.drapuria.framework.bukkit.reflection.resolver.ResolverQuery;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ChatComponentWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.FieldWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.MethodWrapper;
import net.drapuria.framework.bukkit.reflection.util.SubclassUtil;
import net.drapuria.framework.bukkit.reflection.version.PlayerVersion;
import net.drapuria.framework.bukkit.reflection.version.protocol.ProtocolCheck;
import net.drapuria.framework.util.EquivalentConverter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import net.drapuria.framework.bukkit.reflection.resolver.ConstructorResolver;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;
import net.drapuria.framework.bukkit.reflection.resolver.MethodResolver;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.NMSClassResolver;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.OBCClassResolver;
import net.drapuria.framework.util.AccessUtil;
import org.bukkit.entity.Player;
import org.imanity.framework.reflect.ReflectLookup;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Helper class to access minecraft/bukkit specific objects
 */
public class Minecraft {

    public static final Pattern NUMERIC_VERSION_PATTERN = Pattern.compile("v([0-9])_([0-9]*)_R([0-9])");

    /**
     * @deprecated use {@link MinecraftVersion#VERSION} instead
     */
    @Deprecated
    public static final Version VERSION;
    public static final MinecraftVersion MINECRAFT_VERSION = MinecraftVersion.VERSION;

    public static String NETTY_PREFIX;


    private static final NMSClassResolver NMS_CLASS_RESOLVER = new NMSClassResolver();
    private static final OBCClassResolver OBC_CLASS_RESOLVER = new OBCClassResolver();
    private static Class<?> NMS_ENTITY;
    private static Class<?> CRAFT_ENTITY;

    /**
     * The CraftPlayer.getHandle method
     */
    private static MethodWrapper PLAYER_GET_HANDLE;

    /**
     * The EntityPlayer.playerConnection field
     */
    private static FieldWrapper FIELD_PLAYER_CONNECTION;

    /**
     * The PlayerConnection.networkManager field
     */
    private static FieldWrapper FIELD_NETWORK_MANAGER;

    /**
     * The NetworkManager.channel field
     */
    private static FieldWrapper FIELD_CHANNEL;

    /**
     * Netty Channel Type
     */
    public static Class<?> CHANNEL_TYPE;

    /**
     * GameProfile Type
     */
    public static Class<?> GAME_PROFILE_TYPE;

    /**
     * The PlayerConnection.sendPacket method
     */
    private static MethodWrapper<Void> METHOD_SEND_PACKET;

    private static ProtocolCheck PROTOCOL_CHECK;


    static {
        Version tempVersion = Version.UNKNOWN;
        try {
            tempVersion = Version.getVersion();
        } catch (Exception e) {
            System.out.println("[Drapuria] Failed to get legacy version");
        }
        VERSION = tempVersion;
        try {
            Version.runSanityCheck();
        } catch (Exception e) {
            throw new RuntimeException("Sanity check which should always succeed just failed! Am I crazy?!", e);
        }
        try {
            NMS_ENTITY = NMS_CLASS_RESOLVER.resolve("Entity");
            CRAFT_ENTITY = OBC_CLASS_RESOLVER.resolve("entity.CraftEntity");
        } catch (ReflectiveOperationException e) {
          try {
              NMS_ENTITY = NMS_CLASS_RESOLVER.resolve("Entity", "world.entity.Entity");
              CRAFT_ENTITY = OBC_CLASS_RESOLVER.resolve("entity.CraftEntity");
          } catch (ReflectiveOperationException e1) {
              throw new RuntimeException(e1);
          }
        }

        try {
            CHANNEL_TYPE = Class.forName("io.netty.channel.Channel");
            NETTY_PREFIX = "io.netty.";
        } catch (ClassNotFoundException ex) {

            try {
                CHANNEL_TYPE = Class.forName("net.minecraft.util.io.netty.channel.Channel");
                NETTY_PREFIX = "net.minecraft.util.io.netty.";
            } catch (ClassNotFoundException ex2) {
                throw new IllegalStateException("Coulnd't find netty Channel class!", ex2);
            }

        }

        try {
            GAME_PROFILE_TYPE = Class.forName("com.mojang.authlib.GameProfile");
        } catch (ClassNotFoundException ex) {
            try {
                GAME_PROFILE_TYPE = Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
            } catch (ClassNotFoundException ex2) {
                throw new IllegalStateException("Coulnd't find mojang GameProfile class!", ex2);
            }
        }

        try {
            Class<?> entityPlayerType = NMS_CLASS_RESOLVER.resolve("EntityPlayer", "server.level.EntityPlayer");
            Class<?> playerConnectionType = NMS_CLASS_RESOLVER.resolve("PlayerConnection", "server.network.PlayerConnection");
            Class<?> networkManagerType = NMS_CLASS_RESOLVER.resolve("NetworkManager", "network.NetworkManager");

            Minecraft.PLAYER_GET_HANDLE = new MethodWrapper(OBC_CLASS_RESOLVER.resolve("entity.CraftPlayer")
                    .getDeclaredMethod("getHandle"));
            Minecraft.FIELD_PLAYER_CONNECTION = new FieldResolver(entityPlayerType)
                    .resolveByFirstTypeDynamic(playerConnectionType);

            Class<?> packetClass = NMS_CLASS_RESOLVER.resolve("Packet", "network.protocol.Packet");
            final MethodResolver sendPacketResolver = new MethodResolver(playerConnectionType);
            Minecraft.METHOD_SEND_PACKET = new MethodWrapper<>(sendPacketResolver.resolve(new ResolverQuery(0, packetClass)));

            try {
                Minecraft.FIELD_NETWORK_MANAGER = new FieldResolver(playerConnectionType)
                        .resolveByFirstTypeWrapper(networkManagerType);
            } catch (Throwable throwable) {
                Class<?> serverCommonPacketListener = NMS_CLASS_RESOLVER.resolve("server.network.ServerCommonPacketListenerImpl");
                Minecraft.FIELD_NETWORK_MANAGER = new FieldWrapper(new FieldResolver(serverCommonPacketListener)
                        .resolveByFirstExtendingType(networkManagerType));
            }

            Minecraft.FIELD_CHANNEL = new FieldResolver(networkManagerType).resolveByFirstTypeDynamic(CHANNEL_TYPE);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Something went wrong when doing reflection", throwable);
        }

        Minecraft.initProtocolCheck();
    }

    @SneakyThrows
    private static void initProtocolCheck() {

        ReflectLookup reflectLookup = new ReflectLookup(Collections.singletonList(ServerImplementation.class.getClassLoader()),
                Collections.singletonList("net.drapuria.framework.bukkit"));

        Class<?> lastSuccess = null;
        lookup:
        for (Class<?> type : reflectLookup.findAnnotatedClasses(ProtocolImpl.class)) {
            if (!ProtocolCheck.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException("The type " + type.getName() + " does not implement to ProtocolCheck!");
            }

            ImplementationFactory.TestResult result = ImplementationFactory.test(type.getAnnotation(ProviderTestImpl.class));
            switch (result) {
                case NO_PROVIDER:
                    lastSuccess = type;
                    break;
                case SUCCESS:
                    lastSuccess = type;
                    break lookup;
                case FAILURE:
                    break;
            }
        }

        if (lastSuccess == null) {
            throw new UnsupportedOperationException("Couldn't find any usable protocol check! (but it's shouldn't be possible)");
        }

        PROTOCOL_CHECK = (ProtocolCheck) lastSuccess.newInstance();
        Drapuria.LOGGER.info("Initialized Protocol Check with " + lastSuccess.getSimpleName());
    }

    /**
     * @return the current NMS/OBC version (format <code>&lt;version&gt;.</code>
     */
    @Deprecated
    public static String getVersion() {
        return MINECRAFT_VERSION.packageName() + ".";
    }

    /**
     * @return the current NMS version package
     */
    public static String getNMSPackage() {
        return MINECRAFT_VERSION.getNmsPackage();
    }

    /**
     * @return the current OBC package
     */
    public static String getOBCPackage() {
        return MINECRAFT_VERSION.getObcPackage();
    }

    public static Object getHandle(Object object) throws ReflectiveOperationException {
        Method method;
        try {
            method = AccessUtil.setAccessible(object.getClass().getDeclaredMethod("getHandle"));
        } catch (ReflectiveOperationException e) {
            method = AccessUtil.setAccessible(CRAFT_ENTITY.getDeclaredMethod("getHandle"));
        }
        return method.invoke(object);
    }

    public static PlayerVersion getProtocol(Player player) {
        return PlayerVersion.getVersionFromRaw(PROTOCOL_CHECK.getVersion(player));
    }

    public static Entity getBukkitEntity(Object object) throws ReflectiveOperationException {
        Method method;
        try {
            method = AccessUtil.setAccessible(NMS_ENTITY.getDeclaredMethod("getBukkitEntity"));
        } catch (ReflectiveOperationException e) {
            method = AccessUtil.setAccessible(CRAFT_ENTITY.getDeclaredMethod("getHandle"));
        }
        return (Entity) method.invoke(object);
    }

    public static Object getHandleSilent(Object object) {
        try {
            return getHandle(object);
        } catch (Exception e) {
        }
        return null;
    }

    public static EquivalentConverter.EnumConverter<WrappedPacketOutTitle.Action> getTitleActionConverter() {
        if (TITLE_ACTION_CONVERTER == null) {
            TITLE_ACTION_CONVERTER = new EquivalentConverter.EnumConverter<>(getEnumTitleActionClass(), WrappedPacketOutTitle.Action.class);
        }

        return TITLE_ACTION_CONVERTER;
    }

    private static FieldWrapper<Integer> PING_FIELD;

    public static int getPing(Player player) throws ReflectiveOperationException {
        if (PING_FIELD == null) {
            try {
                Class<?> type = NMS_CLASS_RESOLVER.resolve("EntityPlayer", "server.level.EntityPlayer");
                PING_FIELD = new FieldResolver(type).resolveWrapper("ping");
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        Object nmsPlayer = ((Method) getHandle(OBC_CLASS_RESOLVER.resolve("CraftPlayer"))).invoke(player);
        return PING_FIELD.get(nmsPlayer);
    }

    public static <T> T getChannel(Player player) {
        Object entityPlayer = Minecraft.PLAYER_GET_HANDLE.invoke(player);
        Object playerConnection = Minecraft.FIELD_PLAYER_CONNECTION.get(entityPlayer);
        Object networkManager = Minecraft.FIELD_NETWORK_MANAGER.get(playerConnection);
        return (T) Minecraft.FIELD_CHANNEL.get(networkManager);
    }


    public static void sendPacket(Player player, Object packet) {
        Object entityPlayer = Minecraft.PLAYER_GET_HANDLE.invoke(player);
        Object playerConnection = Minecraft.FIELD_PLAYER_CONNECTION.get(entityPlayer);
        Minecraft.METHOD_SEND_PACKET.invoke(playerConnection, packet);
    }

    private static FieldWrapper<Integer> ENTITY_ID_RESOLVER;

    public static int getNewEntityId() {
        return Minecraft.setEntityId(1);
    }

    public static int setEntityId(int newIds) {
        if (ENTITY_ID_RESOLVER == null) {
            ENTITY_ID_RESOLVER = new FieldResolver(NMS_CLASS_RESOLVER.resolveSilent("Entity", "world.entity.Entity"))
                    .resolveWrapper("entityCount");
        }

        int id = ENTITY_ID_RESOLVER.get(null);
        ENTITY_ID_RESOLVER.setSilent(null, id + newIds);
        return id;
    }

    public enum Version {
        UNKNOWN(-1) {
            @Override
            public boolean matchesPackageName(String packageName) {
                return false;
            }
        },

        v1_7_R1(10701),
        v1_7_R2(10702),
        v1_7_R3(10703),
        v1_7_R4(10704),

        v1_8_R1(10801),
        v1_8_R2(10802),
        v1_8_R3(10803),
        //Does this even exists?
        v1_8_R4(10804),

        v1_9_R1(10901),
        v1_9_R2(10902),

        v1_10_R1(11001),

        v1_11_R1(11101),

        v1_12_R1(11201),

        v1_13_R1(11301),
        v1_13_R2(11302),

        v1_14_R1(11401),

        v1_15_R1(11501),

        v1_16_R1(11601),
        v1_16_R2(11602),
        v1_16_R3(11603),

        v1_17_R1(11701),

        v1_18_R1(11801),
        v1_18_R2(11802),

        v1_19_R1(11901),
        v1_19_R2(11902),
        v1_19_R3(11904),
        /// (Potentially) Upcoming versions

        v1_20_R2(12002);


        private final MinecraftVersion version;

        Version(int version, String nmsFormat, String obcFormat, boolean nmsVersionPrefix) {
            this.version = new MinecraftVersion(name(), version, nmsFormat, obcFormat, nmsVersionPrefix);
        }

        Version(int version) {
            if (version >= 11701) { // 1.17+ new class package name format
                this.version = new MinecraftVersion(name(), version, "net.minecraft", "org.bukkit.craftbukkit.%s", false);
            } else {
                this.version = new MinecraftVersion(name(), version);
            }
        }

        /**
         * @return the version-number
         */
        public int version() {
            return version.version();
        }

        /**
         * @param version the version to check
         * @return <code>true</code> if this version is older than the specified version
         */
        @Deprecated
        public boolean olderThan(Version version) {
            return version() < version.version();
        }

        /**
         * @param version the version to check
         * @return <code>true</code> if this version is newer than the specified version
         */
        @Deprecated
        public boolean newerThan(Version version) {
            return version() >= version.version();
        }

        /**
         * @param oldVersion The older version to check
         * @param newVersion The newer version to check
         * @return <code>true</code> if this version is newer than the oldVersion and older that the newVersion
         */
        @Deprecated
        public boolean inRange(Version oldVersion, Version newVersion) {
            return newerThan(oldVersion) && olderThan(newVersion);
        }

        public boolean matchesPackageName(String packageName) {
            return packageName.toLowerCase().contains(name().toLowerCase());
        }

        /**
         * @return the minecraft version
         */
        public MinecraftVersion minecraft() {
            return version;
        }

        static void runSanityCheck() {
            assert v1_14_R1.newerThan(v1_13_R2);
            assert v1_13_R2.olderThan(v1_14_R1);

            assert v1_13_R2.newerThan(v1_8_R1);

            assert v1_13_R2.newerThan(v1_8_R1) && v1_13_R2.olderThan(v1_14_R1);
        }

        @Deprecated
        public static Version getVersion() {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            String versionPackage = name.substring(name.lastIndexOf('.') + 1);
            for (Version version : values()) {
                if (version.matchesPackageName(versionPackage)) {return version;}
            }
            Drapuria.LOGGER.error("[ReflectionHelper] Failed to find version enum for '" + name + "'/'" + versionPackage + "'\"");
            Drapuria.LOGGER.info("[ReflectionHelper] Generating dynamic constant...");
            Matcher matcher = NUMERIC_VERSION_PATTERN.matcher(versionPackage);
            while (matcher.find()) {
                if (matcher.groupCount() < 3) {continue;}

                String majorString = matcher.group(1);
                String minorString = matcher.group(2);
                if (minorString.length() == 1) {minorString = "0" + minorString;}
                String patchString = matcher.group(3);
                if (patchString.length() == 1) {patchString = "0" + patchString;}

                String numVersionString = majorString + minorString + patchString;
                int numVersion = Integer.parseInt(numVersionString);
                String packge = versionPackage;

                try {
                    // Add enum value
                    Field valuesField = new FieldResolver(Version.class).resolve("$VALUES");
                    Version[] oldValues = (Version[]) valuesField.get(null);
                    Version[] newValues = new Version[oldValues.length + 1];
                    System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
                    Version dynamicVersion = (Version) newEnumInstance(Version.class, new Class[]{
                            String.class,
                            int.class,
                            int.class
                    }, new Object[]{
                            packge,
                            newValues.length - 1,
                            numVersion
                    });
                    newValues[newValues.length - 1] = dynamicVersion;
                    valuesField.set(null, newValues);

                    Drapuria.LOGGER.info("[ReflectionHelper] Injected dynamic version " + packge + " (#" + numVersion + ").");
                    Drapuria.LOGGER.info("[ReflectionHelper] Please inform inventivetalent about the outdated version, as this is not guaranteed to work.");
                    return dynamicVersion;
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }

            return UNKNOWN;
        }

        @Override
        public String toString() {
            return name() + " (" + version() + ")";
        }
    }

    @Deprecated
    public static Object newEnumInstance(Class clazz, Class[] types, Object[] values) throws ReflectiveOperationException {
        Constructor constructor = new ConstructorResolver(clazz).resolve(types);
        Field accessorField = new FieldResolver(Constructor.class).resolve("constructorAccessor");
        Object constructorAccessor = accessorField.get(constructor);
        if (constructorAccessor == null) {
            new MethodResolver(Constructor.class).resolve("acquireConstructorAccessor").invoke(constructor);
            constructorAccessor = accessorField.get(constructor);
        }
        return new MethodResolver(constructorAccessor.getClass()).resolve("newInstance").invoke(constructorAccessor, (Object) values);
    }

    public static Class<? extends Enum> getEnumGamemodeClass() {
        try {
            return NMS_CLASS_RESOLVER.resolve("EnumGamemode");
        } catch (Throwable throwable) {
            try {
                Class<? extends Enum> type = NMS_CLASS_RESOLVER.resolve("WorldSettings$EnumGamemode", "world.level.EnumGamemode");
                //    NMS_CLASS_RESOLVER.cache("EnumGamemode", type);
                return type;
            } catch (Throwable throwable1) {
                throw new RuntimeException(throwable1);
            }
        }
    }

    public static Class<?> getIChatBaseComponentClass() {
        try {
            return NMS_CLASS_RESOLVER.resolve("IChatBaseComponent");
        } catch (ClassNotFoundException ex) {
            try {
                return OBC_CLASS_RESOLVER
                        .resolve("util.CraftChatMessage")
                        .getMethod("fromString", String.class)
                        .getReturnType().getComponentType();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }

    public static Class<?> getChatModifierClass() {
        try {
            return NMS_CLASS_RESOLVER.resolve("ChatModifier");
        } catch (Throwable throwable) {
            try {
                return NMS_CLASS_RESOLVER.resolveSubClass(getIChatBaseComponentClass(), "ChatModifier");
            } catch (Throwable throwable1) {
                throw new RuntimeException(throwable1);
            }
        }
    }

    public static Class<? extends Enum> getEnumChatFormatClass() {
        try {
            return NMS_CLASS_RESOLVER.resolve("EnumChatFormat");
        } catch (Throwable throwable) {
            try {
                Class<? extends Enum> type = NMS_CLASS_RESOLVER.resolveSubClass(getIChatBaseComponentClass(), "EnumChatFormat");
                // NMS_CLASS_RESOLVER.cache("ChatModifier", type);
                return type;
            } catch (Throwable throwable1) {
                throw new RuntimeException(throwable1);
            }
        }
    }


    public static Class<? extends Enum> getEnumScoreboardActionClass() {
        try {

            return NMS_CLASS_RESOLVER.resolve("EnumScoreboardAction");
        } catch (Throwable throwable) {
            try {
                Class<? extends Enum> type = NMS_CLASS_RESOLVER.resolveSubClass(PacketTypeClasses.Server.SCOREBOARD_SCORE, "EnumScoreboardAction");
                //    NMS_CLASS_RESOLVER.cache("EnumScoreboardAction", type);
                return type;
            } catch (Throwable throwable1) {
                try {
                    Class<?> c1 = Reflection.getClassByNameWithoutException("net.minecraft.server.ScoreboardServer");
                    Class<? extends Enum> type = SubclassUtil.getEnumSubClass(c1, "Action");
                    return type;
                } catch (Exception throwable2) {
                    throw new RuntimeException(throwable2);
                }
            }
        }
    }


    public static Class<? extends Enum> getEnumTitleActionClass() {
        try {
            return NMS_CLASS_RESOLVER.resolve("EnumTitleAction");
        } catch (Throwable throwable) {
            try {
                Class<? extends Enum> type = NMS_CLASS_RESOLVER.resolveSubClass(PacketTypeClasses.Server.TITLE, "EnumTitleAction");
                NMS_CLASS_RESOLVER.cache("EnumTitleAction", type);
                return type;
            } catch (Throwable throwable1) {
                throw new RuntimeException(throwable1);
            }
        }
    }


    private static EquivalentConverter.EnumConverter<GameMode> GAME_MODE_CONVERTER;
    private static EquivalentConverter.EnumConverter<ChatColor> CHAT_COLOR_CONVERTER;
    private static EquivalentConverter.EnumConverter<WrappedPacketOutScoreboardObjective.HealthDisplayType> HEALTH_DISPLAY_TYPE_CONVERTER;
    private static EquivalentConverter.EnumConverter<WrappedPacketOutScoreboardScore.ScoreboardAction> SCOREBOARD_ACTION_CONVERTER;
     private static EquivalentConverter.EnumConverter<WrappedPacketOutTitle.Action> TITLE_ACTION_CONVERTER;

      private static EquivalentConverter<ChatComponentWrapper> CHAT_COMPONENT_CONVERTER;

    public static EquivalentConverter.EnumConverter<WrappedPacketOutScoreboardObjective.HealthDisplayType> getHealthDisplayTypeConverter() {
        if (HEALTH_DISPLAY_TYPE_CONVERTER == null) {
            HEALTH_DISPLAY_TYPE_CONVERTER = new EquivalentConverter.EnumConverter<>(getHealthDisplayTypeClass(), WrappedPacketOutScoreboardObjective.HealthDisplayType.class);
        }
        return HEALTH_DISPLAY_TYPE_CONVERTER;
    }

    public static EquivalentConverter.EnumConverter<WrappedPacketOutScoreboardScore.ScoreboardAction> getScoreboardActionConverter() {
        if (SCOREBOARD_ACTION_CONVERTER == null) {
            SCOREBOARD_ACTION_CONVERTER = new EquivalentConverter.EnumConverter<>(getEnumScoreboardActionClass(), WrappedPacketOutScoreboardScore.ScoreboardAction.class);
        }
        return SCOREBOARD_ACTION_CONVERTER;
    }

    public static Class<? extends Enum> getHealthDisplayTypeClass() {
        try {
            return NMS_CLASS_RESOLVER.resolve("EnumScoreboardHealthDisplay");
        } catch (Throwable throwable) {
            try {
                Class<? extends Enum> type = NMS_CLASS_RESOLVER.resolve("IScoreboardCriteria$EnumScoreboardHealthDisplay");
                //NMS_CLASS_RESOLVER.cache("EnumScoreboardHealthDisplay", type);
                return type;
            } catch (Throwable throwable1) {
                try {
                    Class<? extends Enum> type = NMS_CLASS_RESOLVER.resolve("net.minecraft.world.scores.criteria.IScoreboardCriteria$EnumScoreboardHealthDisplay");
                    //   NMS_CLASS_RESOLVER.cache("EnumScoreboardHealthDisplay", type);
                    return type;
                } catch (Exception e) {
                    throw new RuntimeException(throwable1);
                }
            }
        }
    }


    public static EquivalentConverter.EnumConverter<GameMode> getGameModeConverter() {
        if (GAME_MODE_CONVERTER == null) {
            GAME_MODE_CONVERTER = new EquivalentConverter.EnumConverter<GameMode>(getEnumGamemodeClass(), GameMode.class) {
                @Nullable
                @Override
                public Object getDefaultGeneric() {
                    try {
                        return Enum.valueOf(this.getGenericType(), "NOT_SET");
                    } catch (IllegalArgumentException ex) { // 1.7
                        return Enum.valueOf(this.getGenericType(), "NONE");
                    }
                }
            };
        }

        return GAME_MODE_CONVERTER;
    }

    public static EquivalentConverter.EnumConverter<ChatColor> getChatColorConverter() {
        if (CHAT_COLOR_CONVERTER == null) {
            Class<? extends Enum> enumChatFormat = getEnumChatFormatClass();

            CHAT_COLOR_CONVERTER = new EquivalentConverter.EnumConverter<>(enumChatFormat, ChatColor.class);
        }

        return CHAT_COLOR_CONVERTER;
    }

    public static EquivalentConverter<ChatComponentWrapper> getChatComponentConverter() {
        if (CHAT_COMPONENT_CONVERTER != null) {
            return CHAT_COMPONENT_CONVERTER;
        }

        return CHAT_COMPONENT_CONVERTER = handle(ChatComponentWrapper::getHandle, ChatComponentWrapper::fromHandle);
    }

    public static <T> EquivalentConverter<T> handle(final Function<T, Object> toHandle,
                                                    final Function<Object, T> fromHandle) {
        return new EquivalentConverter<T>() {
            @Override
            public T getSpecific(Object generic) {
                return fromHandle.apply(generic);
            }

            @Override
            public Object getGeneric(T specific) {
                return toHandle.apply(specific);
            }

            @Override
            public Class<T> getSpecificType() {
                return null;
            }
        };
    }

}
