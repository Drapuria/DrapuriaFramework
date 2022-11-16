package net.drapuria.framework.bukkit.fake.entity.living;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.fake.entity.FakeEntity;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityOptions;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityPool;
import net.drapuria.framework.bukkit.fake.entity.helper.DataWatchHelper;
import net.drapuria.framework.bukkit.fake.entity.living.modifier.LivingFakeEntityEquipmentModifier;
import net.drapuria.framework.bukkit.fake.entity.living.modifier.LivingFakeEntityPositionModifier;
import net.drapuria.framework.bukkit.fake.entity.living.modifier.LivingFakeEntityVisibilityModifier;
import net.drapuria.framework.bukkit.fake.hologram.FakeEntityHologram;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramHelper;
import net.drapuria.framework.bukkit.fake.hologram.line.TextLine;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class LivingFakeEntity extends FakeEntity {

    private EntityType entityType;
    private WrappedDataWatcher dataWatcher;
    private ItemStack itemInHand;

    public LivingFakeEntity(int entityId, FakeEntityOptions options, final Location location, final FakeEntityPool entityPool, final EntityType entityType) {
        super(entityId, location, entityPool, options);
        this.entityType = entityType;
        this.dataWatcher = DataWatchHelper.createDefaultWatcher(this);
        super.hologram = new FakeEntityHologram(this);
        super.hologram.addLine(new TextLine(HologramHelper.newId(), this.options.getDisplayName()));
    }

    @Override
    public void setInvisible(boolean invisible) {
        //..
    }

    @Override
    public void moveTo(Location location) {
        this.location = location;
        this.positionModifier().queuePositionUpdate().send(this.seeingPlayers);
        super.hologram.updateLocation();
    }

    @Override
    public void show(Player player) {
        super.seeingPlayers.add(player);
        visibilityModifier()
                .queueSpawn()
                .send(player);
        if (this.options.isPlayerLook()) this.tickActionForPlayer(player);
        if (this.itemInHand != null)
            equipmentModifier().queue(EnumWrappers.ItemSlot.MAINHAND, this.itemInHand).send(player);
        if (super.hologram != null)
            super.hologram.show(player);
    }

    @Override
    public void hide(Player player) {
        visibilityModifier()
                .queueDestroy()
                .send(player);
        super.seeingPlayers.remove(player);
        if (super.hologram != null)
            super.hologram.hide(player);
    }

    public void setItemInHand(ItemStack itemInHand) {
        this.itemInHand = itemInHand;
        equipmentModifier().queue(EnumWrappers.ItemSlot.MAINHAND, itemInHand)
                .send(super.seeingPlayers);
    }

    public ItemStack getItemInHand() {
        return itemInHand;
    }

    public LivingFakeEntityVisibilityModifier visibilityModifier() {
        return new LivingFakeEntityVisibilityModifier(this);
    }

    public LivingFakeEntityPositionModifier positionModifier() {
        return new LivingFakeEntityPositionModifier(this);
    }

    public LivingFakeEntityEquipmentModifier equipmentModifier() {
        return new LivingFakeEntityEquipmentModifier(this);
    }

    @Override
    public void tickActionForPlayer(Player player) {
        if (isRespawning()) return;
        if (options.isPlayerLook())
            positionModifier().queueLookAt(player.getLocation())
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


    public void setEntityType(EntityType entityType) {
        DrapuriaCommon.TASK_SCHEDULER.runSync(() -> {
            LivingFakeEntity.this.entityType = entityType;
            LivingFakeEntity.this.dataWatcher = DataWatchHelper.createDefaultWatcher(LivingFakeEntity.this);
        });
    }

}