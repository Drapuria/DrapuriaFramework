package net.drapuria.framework.bukkit.fake.entity.living;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.fake.entity.FakeEntity;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityOptions;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityPool;
import net.drapuria.framework.bukkit.fake.entity.helper.DataWatchHelper;
import net.drapuria.framework.bukkit.fake.entity.living.modifier.LivingFakeEntityRotationModifier;
import net.drapuria.framework.bukkit.fake.entity.living.modifier.LivingFakeEntityVisibilityModifier;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@Getter
public class LivingFakeEntity extends FakeEntity {

    private EntityType entityType;
    private final WrappedDataWatcher dataWatcher;

    public LivingFakeEntity(int entityId, FakeEntityOptions options, final Location location, final FakeEntityPool entityPool, final EntityType entityType) {
        super(entityId, location, entityPool, options);
        this.entityType = entityType;
        this.dataWatcher = DataWatchHelper.createDefaultWatcher(this);
    }

    @Override
    public void show(Player player) {
        player.sendMessage("SHOWING?");
        super.seeingPlayers.add(player);
        new LivingFakeEntityVisibilityModifier(this)
                .queueSpawn()
                .send(player);
        if (this.options.isPlayerLook())
            this.tickActionForPlayer(player);
    }

    @Override
    public void hide(Player player) {
        new LivingFakeEntityVisibilityModifier(this)
                .queueDestroy()
                .send(player);
        super.seeingPlayers.remove(player);
    }

    @Override
    public void tickActionForPlayer(Player player) {
        rotation().queueLookAt(player.getLocation())
                .send(player);
    }

    @Override
    public void respawn() {
        super.setRespawning(true);
        DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> {
            ImmutableSet.copyOf(this.seeingPlayers).forEach(this::hide);
            DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> super.setRespawning(false), 20);
        }, 20L);
    }

    public LivingFakeEntityRotationModifier rotation() {
        return new LivingFakeEntityRotationModifier(this);
    }

}