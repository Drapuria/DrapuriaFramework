package net.drapuria.framework.bukkit.fake.entity.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.fake.entity.FakeEntity;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityOptions;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityPool;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityService;
import net.drapuria.framework.bukkit.fake.entity.FakeEntitySpawnHandler;
import net.drapuria.framework.bukkit.fake.entity.npc.inventory.NPCInventory;
import net.drapuria.framework.bukkit.fake.entity.npc.modifier.*;
import net.drapuria.framework.bukkit.fake.hologram.FakeEntityHologram;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramHelper;
import net.drapuria.framework.bukkit.fake.hologram.line.TextLine;
import net.drapuria.framework.bukkit.protocol.packet.PacketService;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutScoreboardTeam;
import net.drapuria.framework.bukkit.protocol.protocollib.ProtocolLibService;
import net.drapuria.framework.bukkit.reflection.minecraft.MinecraftVersion;
import net.drapuria.framework.bukkit.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public class NPC extends FakeEntity {

    private static final ProtocolLibService protocolService = ProtocolLibService.getService;
    private static final FakeEntityService SERVICE = DrapuriaCommon.getBean(FakeEntityService.class);
    public static final Map<EnumWrappers.ItemSlot, Integer> SLOT_CONVERTER = new HashMap<>();


    static {
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.HEAD, 4);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.CHEST, 3);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.LEGS, 2);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.FEET, 1);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.MAINHAND, 0);
    }

    @Setter
    private WrappedGameProfile gameProfile;
    private final NPCOptions npcOptions;
    @Setter
    private NPCInventory inventory = new NPCInventory(this);
    private transient boolean invisible = false;

    public NPC(int entityId, FakeEntityOptions options, Location location, final FakeEntityPool entityPool, NPCOptions npcOptions) {
        super(entityId, location, entityPool, options);
        this.npcOptions = npcOptions;
        updateName();
        if (!npcOptions.getNpcProfile().isComplete()) {
            npcOptions.getNpcProfile().complete();
        }
        this.gameProfile = this.convertProfile(npcOptions.getNpcProfile());
        Bukkit.getOnlinePlayers().forEach(SERVICE::updateTeamForPlayer);
        if (npcOptions.getNameTagType() == NameTagType.HOLOGRAM) {
            super.hologram = new FakeEntityHologram(this);
            super.hologram.addLine(new TextLine(HologramHelper.newId(), this.options.getDisplayName()));
        }
    }

    public NPCInventory getInventory() {
        return inventory;
    }

    private void updateName() {
        if (this.npcOptions.getNameTagType().isHideHologram()) {
            npcOptions.getNpcProfile().setName((!npcOptions.getNpcProfile().getName().startsWith("§r") ? "§r" : "") + npcOptions.getNpcProfile().getName());
        }
        if (npcOptions.getNpcProfile().isComplete())
            npcOptions.getNpcProfile().complete();
    }

    @Override
    public void show(Player player) {
        if (isRespawning()) return;
        super.seeingPlayers.add(player);
        final NPCVisibilityModifier visibilityModifier = this.visibilityModifier();
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        visibilityModifier.queuePlayerListChange(craftPlayer, EnumWrappers.PlayerInfoAction.ADD_PLAYER)
                .send(player);
        if (this.npcOptions.getSkinType() == SkinType.OWN || this.npcOptions.getNameTagType().isHideHologram()) {
            WrappedPacketOutScoreboardTeam packet = SERVICE.getScoreboardTeamPacket(player);
            packet.setAction(3);
            packet.setNameSet(Collections.singletonList(this.npcOptions.getSkinType() == SkinType.OWN ? ("§r" + player.getUniqueId().toString().substring(0, 6)) : this.gameProfile.getName()));
           // protocolService.sendPacket(player, packet.asProtocolLibPacketContainer());
            PacketService.send(player, packet);
        }
        SERVICE.getExecutorService().schedule(() -> {
            if (!player.isOnline()) return;
            visibilityModifier.queueSpawn(craftPlayer).send(player);
            if (this.invisible) {
                metadataModifier().queue(NPCMetadataModifier.EntityMetadata.INVISIBILITY, true).send(player);
                visibilityModifier().queueShowHalfVisible(player).send(player);
            }
            if (super.hologram != null && this.npcOptions.getNameTagType() == NameTagType.HOLOGRAM)
                super.hologram.show(player);
            SERVICE.getExecutorService().schedule(() -> {
                if (!player.isOnline()) return;
                visibilityModifier.queuePlayerListChange(craftPlayer, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER).send(player);
                metadataModifier().queue(NPCMetadataModifier.EntityMetadata.SKIN_LAYERS, true).send(player);
                if (!super.options.isPlayerLook())
                    animationModifier().queue(NPCAnimationModifier.EntityAnimation.SWING_MAIN_ARM).send(player);
                else
                    positionModifier().queueLookAt(player.getLocation())
                            .send(player);
                for (FakeEntitySpawnHandler spawnHandler : options.getSpawnHandlers()) {
                    spawnHandler.onSpawn(player);
                }
            }, entityPool.getTabListRemoveMillis(), TimeUnit.MILLISECONDS);
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void hide(Player player) {
        super.seeingPlayers.remove(player);
        visibilityModifier()
                .queuePlayerListChange((CraftPlayer) player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER)
                .queueDestroy()
                .send(player);
        if (super.hologram != null)
            super.hologram.hide(player);
    }

    @Override
    public void updateHologram() {
        if (super.hologram != null)
            super.hologram.checkHologram();
    }

    public void sneak(final Player player, final boolean sneaking) {
        if (isRespawning()) return;
        metadataModifier().queue(NPCMetadataModifier.EntityMetadata.SNEAKING, sneaking)
                .send(player);
        if (sneaking)
            hologram.setPlayerLocation(player, location.clone().add(0, getHologramHeight() - (MinecraftVersion.VERSION.simpleVersion() < 9 ? 0.4 : 0.5), 0));
        else
            hologram.setPlayerLocation(player, null);
    }

    @Override
    public void tickActionForPlayer(Player player) {
        if (isRespawning()) return;
        if (options.isPlayerLook())
            positionModifier().queueLookAt(player.getLocation()).send(player);
    }

    @Override
    public void respawn() {
        super.setRespawning(true);
        DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> {
            ImmutableSet.copyOf(this.seeingPlayers).forEach(this::hide);
            for (final Player online : Bukkit.getOnlinePlayers()) {
                super.entityPool.updateTeamForPlayer(online);
            }
            DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> {
                DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> super.setRespawning(false), 20);
            }, 100);
        }, 20L);
    }

    public void refreshNpcProfile() {
        this.gameProfile = convertProfile(this.npcOptions.getNpcProfile());
    }

    private WrappedGameProfile convertProfile(NPCProfile npcProfile) {
        WrappedGameProfile gameProfile = new WrappedGameProfile(npcProfile.getUniqueId(),
                npcProfile.getName());
        if (gameProfile.getProperties().isEmpty() && npcProfile.isComplete())
            gameProfile = WrappedGameProfile.fromHandle(ReflectionUtils.fillProfileProperties(gameProfile.getHandle()));
        WrappedGameProfile finalGameProfile = gameProfile;
        npcProfile.getProperties().forEach(property -> finalGameProfile.getProperties().put(property.getName(), property.asWrapped()));
        return finalGameProfile;
    }

    @Override
    public void setInvisible(boolean invisible) {
        if (this.invisible != invisible) {
            this.invisible = invisible;
            metadataModifier().queue(NPCMetadataModifier.EntityMetadata.INVISIBILITY, this.invisible).send(seeingPlayers.toArray(new Player[0]));
            if (this.invisible)
                seeingPlayers.forEach(player -> visibilityModifier().queueShowHalfVisible(player).send(player));
            else
                seeingPlayers.forEach(player -> visibilityModifier().queueHideHalfVisible(player).send(player));
        }
    }

    public void setHalfInvisible(boolean halfInvisible) {

    }

    @Override
    public void moveTo(Location location) {
        this.location = location;
        if (this.hologram != null)
            this.hologram.updateLocation();
        this.positionModifier()
                .queuePositionUpdate()
                .queueRotate(this.location.getYaw(), this.location.getPitch())
                .send(this.seeingPlayers);
    }

    public NPCVisibilityModifier visibilityModifier() {
        return new NPCVisibilityModifier(this);
    }

    public NPCMetadataModifier metadataModifier() {
        return new NPCMetadataModifier(this);
    }

    public NPCAnimationModifier animationModifier() {
        return new NPCAnimationModifier(this);
    }

    public NPCPositionModifier positionModifier() {
        return new NPCPositionModifier(this);
    }

    public NPCEquipmentModifier equipmentModifier() {
        return new NPCEquipmentModifier(this);
    }

}
