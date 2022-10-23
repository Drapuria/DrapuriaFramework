package net.drapuria.framework.bukkit.fake.entity.living.modifier;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import net.drapuria.framework.bukkit.fake.entity.living.LivingFakeEntity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class LivingFakeEntityVisibilityModifier extends LivingFakeEntityModifier {
    public LivingFakeEntityVisibilityModifier(@NotNull LivingFakeEntity fakeEntity) {
        super(fakeEntity);
    }

    public LivingFakeEntityVisibilityModifier queueSpawn() {
        if (super.fakeEntity.getEntityType().isAlive()) {
            final PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING, true);
            packetContainer.getIntegers().write(1, (int) this.fakeEntity.getEntityType().getTypeId());
            packetContainer.getIntegers().write(2, (int) Math.floor(super.fakeEntity.getLocation().getX() * 32D));
            packetContainer.getIntegers().write(3, (int) Math.floor((super.fakeEntity.getLocation().getY() + 0.001D) * 32D));
            packetContainer.getIntegers().write(4, (int) Math.floor(super.fakeEntity.getLocation().getZ() * 32D));

            packetContainer.getBytes().write(0, (byte) (int) (super.fakeEntity.getLocation().getYaw() * 256.0F / 360.0F));
            packetContainer.getBytes().write(1, (byte) (int) (super.fakeEntity.getLocation().getPitch() * 256.0F / 360.0F));
            packetContainer.getBytes().write(2, (byte) (int) (super.fakeEntity.getLocation().getYaw() * 256.0F / 360.0F));

            packetContainer.getDataWatcherModifier().write(0, this.fakeEntity.getDataWatcher());
        } else {
            final PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.SPAWN_ENTITY);
            packetContainer.getIntegers().write(1, (int) Math.floor(super.fakeEntity.getLocation().getX() * 32.0D));
            packetContainer.getIntegers().write(2, (int) Math.floor((super.fakeEntity.getLocation().getY() + 0.001D) * 32.0D));
            packetContainer.getIntegers().write(3, (int) Math.floor(super.fakeEntity.getLocation().getZ() * 32.0D));
            packetContainer.getIntegers().write(4, 0);
            packetContainer.getIntegers().write(5, 0);
            packetContainer.getIntegers().write(6, 0);
            packetContainer.getIntegers().write(7, (int) (super.fakeEntity.getLocation().getYaw() * 256.0D / 360.0D));
            packetContainer.getIntegers().write(8, (int) (super.fakeEntity.getLocation().getPitch() * 256.0D / 360.0D));
            packetContainer.getIntegers().write(9, getIdForEntity(super.fakeEntity.getEntityType()));
            packetContainer.getIntegers().write(10, 0);
        }
        return this;
    }

    public LivingFakeEntityVisibilityModifier queueDestroy() {
        final PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.ENTITY_DESTROY, false);
        packetContainer.getIntegerArrays().write(0, new int[]{super.fakeEntity.getEntityId()});
        return this;
    }

    private static int getIdForEntity(EntityType type) {
        switch (type) {
            case BOAT:
                return 1;
            case MINECART:
                return 10;
            case ENDER_CRYSTAL:
                return 51;
            case FIREBALL:
                return 63;
            case SMALL_FIREBALL:
                return 64;
            case WITHER_SKULL:
                return 66;
            case ARMOR_STAND:
                return 78;
        }
        return -1;
    }

}
