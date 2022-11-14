package net.drapuria.framework.bukkit.fake.entity.living.modifier;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import net.drapuria.framework.bukkit.fake.entity.living.LivingFakeEntity;
import net.drapuria.framework.bukkit.fake.entity.modifier.FakeEntityModifier;
import net.drapuria.framework.bukkit.fake.entity.npc.modifier.NPCPositionModifier;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("DuplicatedCode")
public class LivingFakeEntityPositionModifier extends FakeEntityModifier<LivingFakeEntity> {
    public LivingFakeEntityPositionModifier(@NotNull LivingFakeEntity fakeEntity) {
        super(fakeEntity);
    }

    public LivingFakeEntityPositionModifier queueRotation(float yaw, float pitch) {
        byte yawAngle = (byte) (int) (yaw * 256F / 360F);
        byte pitchAngle = (byte) (int) (pitch * 256F / 360F);
        PacketContainer headLookContainer = super
                .newContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        headLookContainer.getBytes().write(0, yawAngle);
        final PacketContainer bodyRotateContainer;
        if (MINECRAFT_VERSION.olderThan(Minecraft.Version.v1_9_R2)) {
            bodyRotateContainer = super.newContainer(PacketType.Play.Server.ENTITY_TELEPORT);

            Location location = super.fakeEntity.getLocation();
            bodyRotateContainer.getIntegers()
                    .write(1, (int) Math.floor(location.getX() * 32.0D))
                    .write(2, (int) Math.floor(location.getY() * 32.0D))
                    .write(3, (int) Math.floor(location.getZ() * 32.0D));
        } else {
            bodyRotateContainer = super.newContainer(PacketType.Play.Server.ENTITY_LOOK);
        }
        final PacketContainer lookContainer = super.newContainer(PacketType.Play.Server.ENTITY_LOOK);
        lookContainer.getBytes()
                .write(3, yawAngle)
                .write(4, pitchAngle);
        bodyRotateContainer.getBooleans().write(0, true);
        return this;
    }

    public LivingFakeEntityPositionModifier queueBodyRotation(float yaw, float pitch) {
        byte yawAngle = (byte) (int) (yaw * 256F / 360F);
        byte pitchAngle = (byte) (int) (pitch * 256F / 360F);


        PacketContainer bodyRotateContainer;
        if (MINECRAFT_VERSION.olderThan(Minecraft.Version.v1_9_R2)) {
            bodyRotateContainer = super.newContainer(PacketType.Play.Server.ENTITY_LOOK);

            Location location = super.fakeEntity.getLocation();
            bodyRotateContainer.getIntegers()
                    .write(1, (int) Math.floor(location.getX() * 32.0D))
                    .write(2, (int) Math.floor(location.getY() * 32.0D))
                    .write(3, (int) Math.floor(location.getZ() * 32.0D));
        } else {
            bodyRotateContainer = super.newContainer(PacketType.Play.Server.ENTITY_LOOK);
        }

        bodyRotateContainer.getBytes()
                .write(3, yawAngle)
                .write(4, pitchAngle);

        bodyRotateContainer.getBooleans().write(0, true);

        return this;
    }

    public LivingFakeEntityPositionModifier queuePositionUpdate() {
        final PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packetContainer.getIntegers().write(1, (int) Math.floor(fakeEntity.getLocation().getX() * 32)); //X
        packetContainer.getIntegers().write(2, (int) Math.floor((fakeEntity.getLocation().getY() + 0.001D) * 32)); //Y
        packetContainer.getIntegers().write(3, (int) Math.floor(fakeEntity.getLocation().getZ() * 32)); //Z
        packetContainer.getBytes().write(0, (byte) (int) (this.fakeEntity.getLocation().getYaw() * 256.0F / 360.0F)); //Yaw
        packetContainer.getBytes().write(1, (byte) (int) (this.fakeEntity.getLocation().getPitch() * 256.0F / 360.0F)); //Pitch
        return this;
    }

    public LivingFakeEntityPositionModifier queueLookAt(@NotNull Location location) {
        double xDifference = location.getX() - super.fakeEntity.getLocation().getX();
        double yDifference = location.getY() - super.fakeEntity.getLocation().getY();
        double zDifference = location.getZ() - super.fakeEntity.getLocation().getZ();

        double r = Math
                .sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2) + Math.pow(zDifference, 2));

        float yaw = (float) (-Math.atan2(xDifference, zDifference) / Math.PI * 180D);
        yaw = yaw < 0 ? yaw + 360 : yaw;
        float pitch = (float) (-Math.asin(yDifference / r) / Math.PI * 180D);

        return this.queueRotation(yaw, pitch);
    }

}
