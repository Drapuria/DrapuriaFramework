/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.packet.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.protocol.ProtocolService;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.protocollib.ProtocolLibService;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.minecraft.MinecraftVersion;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class WrappedPacketOutScoreboardTeam implements SendableWrapper {

    private static boolean HAS_TEAM_PUSH;
    private static boolean HAS_CHAT_FORMAT;

    private static boolean newVersion = false;

    static {
        FieldResolver fieldResolver = new FieldResolver(PacketTypeClasses.Play.Server.SCOREBOARD_TEAM);
        newVersion = MinecraftVersion.getVersion().newerThan(Minecraft.Version.v1_16_R1);
        try {
            HAS_TEAM_PUSH = fieldResolver.resolve(String.class, 5) != null;
        } catch (IllegalArgumentException ex) {
            HAS_TEAM_PUSH = false;
        }

        try {
            HAS_CHAT_FORMAT = fieldResolver.resolve(int.class, 2) != null;
        } catch (IllegalArgumentException ex) {
            HAS_CHAT_FORMAT = false;
        }

    }

    private String name = "";
    private String displayName = "";
    private String prefix = "";
    private String suffix = "";
    private NameTagVisibility visibility = NameTagVisibility.ALWAYS;
    private EnumTeamPush teamPush = EnumTeamPush.ALWAYS; // 1.9+
    private int chatFormat = 0;
    private Collection<String> nameSet = new ArrayList<>();
    private int action = 0;
    private boolean allowFriendlyFire = true;
    private boolean seeFriendlyInvisibles = true;

    @Override
    public PacketContainer asProtocolLibPacketContainer() {
        final PacketContainer packetContainer = ProtocolLibService.getService.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
        int packOptionData = 0;
        if (this.allowFriendlyFire) {
            packOptionData |= 1;
        }
        if (this.seeFriendlyInvisibles) {
            packOptionData |= 2;
        }
        if (!newVersion) {
            packetContainer.getStrings().write(0, this.name);
            packetContainer.getStrings().write(1, this.displayName);
            packetContainer.getStrings().write(2, this.prefix);
            packetContainer.getStrings().write(3, this.suffix);
            packetContainer.getStrings().write(4, this.visibility.name);
            if (HAS_TEAM_PUSH) {
                packetContainer.getStrings().write(5, this.teamPush.name);
            }
            packetContainer.getSpecificModifier(Collection.class).write(0, this.nameSet);
            if (HAS_CHAT_FORMAT) {
                packetContainer.getIntegers().write(0, this.chatFormat);
                packetContainer.getIntegers().write(1, this.action);
                packetContainer.getIntegers().write(2, packOptionData);
            } else {
                packetContainer.getIntegers().write(0, this.action);
                packetContainer.getIntegers().write(1, packOptionData);
            }
        } else {
            packetContainer.getStrings().write(0, this.name);
            packetContainer.getIntegers().write(0, this.action);
            packetContainer.getSpecificModifier(Collection.class).write(0, this.nameSet);
            packetContainer.getSpecificModifier(Optional.class).write(0, (Optional) ProtocolService.protocolService
                    .getVersionHelper().getScoreboardTeamOptional(this.name,
                            this.displayName,
                            this.prefix,
                            this.suffix,
                            chatFormat,
                            packOptionData,
                            this.allowFriendlyFire,
                            visibility));

        }
        return packetContainer;
    }

    public static enum NameTagVisibility {
        ALWAYS("always", 0),
        NEVER("never", 1),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

        private static final Map<String, NameTagVisibility> nameToTagVisibility = Maps.newHashMap();

        public final String name;
        public final int id;

        public static NameTagVisibility getByName(String name) {
            return nameToTagVisibility.get(name);
        }

        NameTagVisibility(String name, int id) {
            this.name = name;
            this.id = id;
        }

        static {
            for (NameTagVisibility visibility : values()) {
                nameToTagVisibility.put(visibility.name, visibility);
            }
        }
    }

    public static enum EnumTeamPush {
        ALWAYS("always", 0),
        NEVER("never", 1),
        HIDE_FOR_OTHER_TEAMS("pushOtherTeams", 2),
        HIDE_FOR_OWN_TEAM("pushOwnTeam", 3);

        private static final Map<String, EnumTeamPush> nameToTeamPush = Maps.newHashMap();
        public final String name;
        public final int id;

        @Nullable
        public static EnumTeamPush getByName(String name) {
            return nameToTeamPush.get(name);
        }

        private EnumTeamPush(String name, int id) {
            this.name = name;
            this.id = id;
        }

        static {

            for (EnumTeamPush teamPush : values()) {
                nameToTeamPush.put(teamPush.name, teamPush);
            }

        }
    }

    private static class Test implements EquivalentConverter<String> {

        @Override
        public Object getGeneric(String s) {
            return s;
        }

        @Override
        public String getSpecific(Object o) {
            return (String) o;
        }

        @Override
        public Class<String> getSpecificType() {
            return String.class;
        }
    }

}
