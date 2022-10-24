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
import net.drapuria.framework.bukkit.fake.entity.npc.modifier.NPCAnimationModifier;
import net.drapuria.framework.bukkit.fake.entity.npc.modifier.NPCMetadataModifier;
import net.drapuria.framework.bukkit.fake.entity.npc.modifier.NPCVisibilityModifier;
import net.drapuria.framework.bukkit.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public class NPC extends FakeEntity {
    private static final FakeEntityService SERVICE = DrapuriaCommon.getBean(FakeEntityService.class);
    private static final Map<EnumWrappers.ItemSlot, Integer> SLOT_CONVERTER = new HashMap<>();

    static {
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.HEAD, 4);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.CHEST, 3);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.LEGS, 2);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.FEET, 1);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.MAINHAND, 0);
    }

    @Setter
    private WrappedGameProfile gameProfile;
    private NPCOptions npcOptions;

    public NPC(int entityId, FakeEntityOptions options, Location location, final FakeEntityPool entityPool, NPCOptions npcOptions) {
        super(entityId, location, entityPool, options);
        this.npcOptions = npcOptions;
        this.gameProfile = this.convertProfile(npcOptions.getNpcProfile());
    }

    @Override
    public void show(Player player) {
        if (isRespawning()) return;
        super.seeingPlayers.add(player);
        final NPCVisibilityModifier visibilityModifier = this.visibilityModifier();
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        visibilityModifier.queuePlayerListChange(craftPlayer, EnumWrappers.PlayerInfoAction.ADD_PLAYER)
                .send(player);
        // TODO SKIN TYPE OWN
        SERVICE.getExecutorService().schedule(() -> {
            if (!player.isOnline()) return;
            visibilityModifier.queueSpawn(craftPlayer).send(player);
            SERVICE.getExecutorService().schedule(() -> {
                if (!player.isOnline()) return;
                if (!super.options.isPlayerLook())
                    animationModifier().queue(NPCAnimationModifier.EntityAnimation.SWING_MAIN_ARM).send(player);
                metadataModifier().queue(NPCMetadataModifier.EntityMetadata.SKIN_LAYERS, true).send(player);
                visibilityModifier.queuePlayerListChange(craftPlayer, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER).send(player);
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
    }

    @Override
    public void tickActionForPlayer(Player player) {

    }

    @Override
    public void respawn() {
        super.setRespawning(true);
        DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> {
            ImmutableSet.copyOf(this.seeingPlayers).forEach(this::hide);
            DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> {
                for (final Player online : Bukkit.getOnlinePlayers()) {
                    super.entityPool.updateTeamForPlayer(online);
                }
                super.setRespawning(false);
            }, 100);
        }, 20L);
    }

    private WrappedGameProfile convertProfile(NPCProfile profile) {
        WrappedGameProfile gameProfile = new WrappedGameProfile(profile.getUniqueId(),
                profile.getName());
        if (gameProfile.getProperties().isEmpty() && profile.isComplete())
            gameProfile = WrappedGameProfile.fromHandle(ReflectionUtils.fillProfileProperties(gameProfile.getHandle()));
        WrappedGameProfile finalGameProfile = gameProfile;
        profile.getProperties().forEach(property -> finalGameProfile.getProperties().put(property.getName(), property.asWrapped()));
        return finalGameProfile;
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

}
