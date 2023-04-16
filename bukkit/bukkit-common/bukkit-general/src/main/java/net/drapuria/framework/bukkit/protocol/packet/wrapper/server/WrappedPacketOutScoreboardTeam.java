package net.drapuria.framework.bukkit.protocol.packet.wrapper.server;

import com.google.common.collect.Maps;
import lombok.*;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.SendableWrapper;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ObjectWrapper;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

@AutowiredWrappedPacket(value = PacketType.Server.SCOREBOARD_TEAM, direction = PacketDirection.WRITE)
@Getter
@Setter
public class WrappedPacketOutScoreboardTeam extends WrappedPacket implements SendableWrapper {

    private static boolean HAS_TEAM_PUSH;
    private static boolean HAS_CHAT_FORMAT;

    public static void init() {

        FieldResolver fieldResolver = new FieldResolver(PacketTypeClasses.Server.SCOREBOARD_TEAM);

        try {
            HAS_TEAM_PUSH = fieldResolver.resolveSilent(String.class, 5).exists();
        } catch (IllegalArgumentException ex) {
            HAS_TEAM_PUSH = false;
        }

        try {
            HAS_CHAT_FORMAT = fieldResolver.resolveSilent(int.class, 2).exists();
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

    public WrappedPacketOutScoreboardTeam(Player player, Object packet) {
        super(player, packet);
    }

    public WrappedPacketOutScoreboardTeam(Object packet) {
        super(packet);
    }

    public WrappedPacketOutScoreboardTeam() {
        super();
    }

    @Override
    protected void setup() {
        this.name = readString(0);
        this.displayName = readString(1);
        this.prefix = readString(2);
        this.suffix = readString(3);
        this.visibility = NameTagVisibility.getByName(readString(4));

        if (HAS_TEAM_PUSH) {
            this.teamPush = EnumTeamPush.getByName(readString(5));
        }

        this.nameSet = readObject(0, Collection.class);

        int packOptionData;

        if (HAS_CHAT_FORMAT) {
            this.chatFormat = readInt(0);
            this.action = readInt(1);
            packOptionData = readInt(2);
        } else {
            this.action = readInt(0);
            packOptionData = readInt(1);
        }

        this.allowFriendlyFire = (packOptionData & 1) > 0;
        this.seeFriendlyInvisibles = (packOptionData & 2) > 0;

    }

    @Override
    public Object asNMSPacket() {

        Object packetObject;

        try {
            packetObject = PacketTypeClasses.Server.SCOREBOARD_TEAM.newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }

        ObjectWrapper packet = new ObjectWrapper(packetObject);
        packet.getFieldWrapperByIndex(String.class, 0).set(packetObject, this.name);
        packet.getFieldWrapperByIndex(String.class, 1).set(packetObject, this.displayName);
        packet.getFieldWrapperByIndex(String.class, 2).set(packetObject, this.prefix);
        packet.getFieldWrapperByIndex(String.class, 3).set(packetObject, this.suffix);
        packet.getFieldWrapperByIndex(String.class, 4).set(packetObject, this.visibility.name);
        if (HAS_TEAM_PUSH) {
            packet.getFieldWrapperByIndex(String.class, 5).set(packetObject, this.teamPush.name);
        }

        packet.getFieldByIndex(Collection.class, 0).addAll(this.nameSet);

        int packOptionData = 0;
        if (this.isAllowFriendlyFire()) {
            packOptionData |= 1;
        }

        if (this.isSeeFriendlyInvisibles()) {
            packOptionData |= 2;
        }

        if (HAS_CHAT_FORMAT) {
            packet.getFieldWrapperByIndex(int.class, 0).set(packetObject, this.chatFormat);
            packet.getFieldWrapperByIndex(int.class, 1).set(packetObject, this.action);
            packet.getFieldWrapperByIndex(int.class, 2).set(packetObject, packOptionData);
        } else {
            packet.getFieldWrapperByIndex(int.class, 0).set(packetObject, this.action);
            packet.getFieldWrapperByIndex(int.class, 1).set(packetObject, packOptionData);
        }

        return packetObject;
    }

    public static WrappedPacketOutScoreboardTeamBuilder builder() {
        return new WrappedPacketOutScoreboardTeamBuilder();
    }

    public static class WrappedPacketOutScoreboardTeamBuilder
    {
        private String name;
        private String displayName;
        private String prefix;
        private String suffix;
        private NameTagVisibility visibility;
        private EnumTeamPush teamPush;
        private int chatFormat = -1;
        private List<String> nameSet;
        private int action = -1;
        private boolean allowFriendlyFire = true;
        private boolean seeFriendlyInvisibles = true;

        public WrappedPacketOutScoreboardTeamBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder displayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder prefix(final String prefix) {
            this.prefix = prefix;
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder suffix(final String suffix) {
            this.suffix = suffix;
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder visibility(final NameTagVisibility visibility) {
            this.visibility = visibility;
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder teamPush(final EnumTeamPush teamPush) {
            this.teamPush = teamPush;
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder chatFormat(final int chatFormat) {
            this.chatFormat = chatFormat;
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder nameSet(final String nameSet) {
            if (this.nameSet == null) {
                this.nameSet = new ArrayList<>();
            }
            this.nameSet.add(nameSet);
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder nameSets(final Collection<? extends String> nameSet) {
            if (this.nameSet == null) {
                this.nameSet = new ArrayList<>();
            }
            this.nameSet.addAll(nameSet);
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder clearNameSet() {
            if (this.nameSet != null) {
                this.nameSet.clear();
            }
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder action(final int action) {
            this.action = action;
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder allowFriendlyFire(final boolean allowFriendlyFire) {
            this.allowFriendlyFire = allowFriendlyFire;
            return this;
        }

        public WrappedPacketOutScoreboardTeamBuilder seeFriendlyInvisibles(final boolean seeFriendlyInvisibles) {
            this.seeFriendlyInvisibles = seeFriendlyInvisibles;
            return this;
        }

        public WrappedPacketOutScoreboardTeam build() {
            WrappedPacketOutScoreboardTeam packet = new WrappedPacketOutScoreboardTeam();

            if (this.name != null) {
                packet.setName(name);
            }

            if (this.displayName != null) {
                packet.setDisplayName(displayName);
            }

            if (this.prefix != null) {
                packet.setPrefix(prefix);
            }

            if (this.suffix != null) {
                packet.setSuffix(suffix);
            }

            if (this.visibility != null) {
                packet.setVisibility(visibility);
            }

            if (this.teamPush != null) {
                packet.setTeamPush(teamPush);
            }

            if (this.chatFormat != -1) {
                packet.setChatFormat(chatFormat);
            }

            if (this.nameSet != null) {
                packet.getNameSet().addAll(this.nameSet);
            }

            if (this.action != -1) {
                packet.setAction(action);
            }

            packet.setAllowFriendlyFire(allowFriendlyFire);
            packet.setSeeFriendlyInvisibles(seeFriendlyInvisibles);

            return packet;
        }

        @Override
        public String toString() {
            return "WrappedPacketOutScoreboardTeam.WrappedPacketOutScoreboardTeamBuilder(name=" + this.name + ", displayName=" + this.displayName + ", prefix=" + this.prefix + ", suffix=" + this.suffix + ", visibility=" + this.visibility + ", teamPush=" + this.teamPush + ", chatFormat=" + this.chatFormat + ", nameSet=" + this.nameSet + ", action=" + this.action + ", allowFriendlyFire=" + this.allowFriendlyFire + ", seeFriendlyInvisibles=" + this.seeFriendlyInvisibles + ")";
        }
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

}
