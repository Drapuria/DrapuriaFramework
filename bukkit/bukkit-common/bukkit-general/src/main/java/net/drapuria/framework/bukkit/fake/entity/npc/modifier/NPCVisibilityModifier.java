package net.drapuria.framework.bukkit.fake.entity.npc.modifier;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityService;
import net.drapuria.framework.bukkit.fake.entity.modifier.FakeEntityModifier;
import net.drapuria.framework.bukkit.fake.entity.npc.NPC;
import net.drapuria.framework.bukkit.fake.entity.npc.SkinType;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutScoreboardTeam;
import net.drapuria.framework.bukkit.protocol.protocollib.ProtocolLibService;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class NPCVisibilityModifier extends FakeEntityModifier<NPC> {

    private static final FakeEntityService SERVICE = FakeEntityService.getService;

    public NPCVisibilityModifier(@NotNull NPC fakeEntity) {
        super(fakeEntity);
    }

    public NPCVisibilityModifier queuePlayerListChange(CraftPlayer player, EnumWrappers.PlayerInfoAction action) {
        final PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.PLAYER_INFO, false);
        packetContainer.getPlayerInfoAction().write(0, action);

        final WrappedGameProfile profile = super.fakeEntity.getNpcOptions().getSkinType() == SkinType.OWN ?
                WrappedGameProfile.fromPlayer(player).withName("§r" + player.getUniqueId().toString().substring(0, 6)).withId(SERVICE.getRandomIdOf(player.getUniqueId()).toString()) :
                super.fakeEntity.getGameProfile();
        if (super.fakeEntity.getNpcOptions().getSkinType() == SkinType.OWN) {
            profile.getProperties().put("textures", WrappedSignedProperty.fromHandle(player.getProfile().getProperties().get("textures").stream().findFirst().orElse(null)));
        }
        final PlayerInfoData playerInfoData = new PlayerInfoData(
                profile,
                20,
                EnumWrappers.NativeGameMode.NOT_SET,
                WrappedChatComponent.fromText("")
        );
        packetContainer.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
        return this;
    }

    public NPCVisibilityModifier queueSpawn(CraftPlayer player) {
        final PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        if (super.fakeEntity.getNpcOptions().getSkinType() == SkinType.DEFAULT) {
            packetContainer.getUUIDs().write(0, super.fakeEntity.getGameProfile().getUUID());
        } else {
            packetContainer.getUUIDs().write(0, SERVICE.getRandomIdOf(player.getUniqueId()));
        }
        final byte yawData = (byte) ((super.fakeEntity.getLocation().getYaw()) * 256.0F / 360.0F);

        final double x = super.fakeEntity.getLocation().getX();
        final double y = super.fakeEntity.getLocation().getY();
        final double z = super.fakeEntity.getLocation().getZ();

        if (MINECRAFT_VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
            packetContainer.getIntegers()
                    .write(1, (int) Math.floor(x * 32.0D))
                    .write(2, (int) Math.floor(y * 32.0D))
                    .write(3, (int) Math.floor(z * 32.0D));
        } else {
            packetContainer.getDoubles()
                    .write(0, x)
                    .write(1, y)
                    .write(2, z);
        }

        packetContainer.getBytes()
                .write(0, yawData)
                .write(1, (byte) (super.fakeEntity.getLocation().getPitch() * 256F / 360F));

        if (MINECRAFT_VERSION.olderThan(Minecraft.Version.v1_15_R1)) {
            packetContainer.getDataWatcherModifier().write(0, new WrappedDataWatcher());
        }
        final PacketContainer headRotation = super.newContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        headRotation.getBytes().write(0, yawData);

        /*
        PacketContainer bodyRotation = super.newContainer(PacketType.Play.Server.ENTITY_LOOK);
        bodyRotation.getBytes()
                .write(3, yawData)
                .write(4, (byte) (super.npc.getLocation().getPitch() * 256.0F / 360.0));
         */
        return this;
    }


    public NPCVisibilityModifier queueShowHalfVisible(final Player player) {
        WrappedPacketOutScoreboardTeam teamPacket = setupTeamScoreboard(player);
        teamPacket.setVisibility(WrappedPacketOutScoreboardTeam.NameTagVisibility.NEVER);
        teamPacket.setAction(2);
        ProtocolLibService.getService.sendPacket(player, teamPacket.asProtocolLibPacketContainer());
        teamPacket.setAction(3);
        teamPacket.setNameSet(Collections.singletonList(this.fakeEntity.getGameProfile().getName()));
        add(teamPacket.asProtocolLibPacketContainer());
        return this;
    }

    public NPCVisibilityModifier queueHideHalfVisible(final Player player) {

        WrappedPacketOutScoreboardTeam teamPacket = setupTeamScoreboard(player);;
        teamPacket.setVisibility(WrappedPacketOutScoreboardTeam.NameTagVisibility.ALWAYS);
        teamPacket.setAction(2);
        ProtocolLibService.getService.sendPacket(player, teamPacket.asProtocolLibPacketContainer());
        teamPacket.setNameSet(Collections.singletonList(this.fakeEntity.getGameProfile().getName()));
        teamPacket.setAction(4);
        ProtocolLibService.getService.sendPacket(player, teamPacket.asProtocolLibPacketContainer());
        WrappedPacketOutScoreboardTeam realTeam = FakeEntityService.getService.getScoreboardTeamPacket(player);
        realTeam.setAction(3);
        realTeam.setNameSet(Collections.singletonList(this.fakeEntity.getGameProfile().getName()));
        add(realTeam.asProtocolLibPacketContainer());
        return this;
    }

    private WrappedPacketOutScoreboardTeam setupTeamScoreboard(Player player) {
        Team pTeam = player.getScoreboard().getEntryTeam(player.getName());
        WrappedPacketOutScoreboardTeam teamPacket = new WrappedPacketOutScoreboardTeam();
        teamPacket.setDisplayName(pTeam.getDisplayName());
        teamPacket.setName(pTeam.getName());
        teamPacket.setPrefix(pTeam.getPrefix());
        teamPacket.setSuffix(pTeam.getSuffix());
        return teamPacket;
    }



    /*
    public NPCVisibilityModifier queueShowHalfVisible(final Player player) {
      //  final PacketContainer teamPacket = super.newContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
       // Team pTeam = player.getScoreboard().getEntryTeam(player.getName());
        WrappedPacketOutScoreboardTeam teamPacket = FakeEntityService.getService.getHalfVisibleScoreboardTeamPacket(player);
        teamPacket.setAction(2);
        teamPacket.setVisibility(WrappedPacketOutScoreboardTeam.NameTagVisibility.NEVER);
        ProtocolLibService.getService.sendPacket(player, teamPacket.asProtocolLibPacketContainer());
        teamPacket.setAction(3);
        teamPacket.setNameSet(Arrays.asList(player.getName(), this.fakeEntity.getGameProfile().getName()));
        add(teamPacket.asProtocolLibPacketContainer());
        return this;
    }

    public NPCVisibilityModifier queueHideHalfVisible(final Player player) {
        WrappedPacketOutScoreboardTeam teamPacket = FakeEntityService.getService.getHalfVisibleScoreboardTeamPacket(player);
        teamPacket.setAction(4);
      //  teamPacket.setNameSet(Collections.singletonList(this.fakeEntity.getGameProfile().getName()));
        teamPacket.setNameSet(Arrays.asList(player.getName(), this.fakeEntity.getGameProfile().getName()));
        ProtocolLibService.getService.sendPacket(player, teamPacket.asProtocolLibPacketContainer());
        teamPacket.setVisibility(WrappedPacketOutScoreboardTeam.NameTagVisibility.ALWAYS);
        teamPacket.setAction(2);
        add(teamPacket.asProtocolLibPacketContainer());
        return this;
    }
     */

    public NPCVisibilityModifier queueDestroy() {
        final PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.ENTITY_DESTROY, false);
        packetContainer.getIntegerArrays().write(0, new int[]{super.fakeEntity.getEntityId()});
        return this;
    }
}