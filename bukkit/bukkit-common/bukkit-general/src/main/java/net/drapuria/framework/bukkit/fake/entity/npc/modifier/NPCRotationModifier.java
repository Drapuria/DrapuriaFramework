package net.drapuria.framework.bukkit.fake.entity.npc.modifier;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import net.drapuria.framework.bukkit.fake.entity.modifier.FakeEntityModifier;
import net.drapuria.framework.bukkit.fake.entity.npc.NPC;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class NPCRotationModifier extends FakeEntityModifier<NPC> {

    public NPCRotationModifier(@NotNull NPC fakeEntity) {
        super(fakeEntity);
    }
    public NPCRotationModifier queueRotate(float yaw, float pitch) {
        byte yawAngle = (byte) (yaw * 256F / 360F);
        byte pitchAngle = (byte) (pitch * 256F / 360F);

        final PacketContainer headLookContainer = super
                .newContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        headLookContainer.getBytes().write(0, yawAngle);

        final PacketContainer bodyRotateContainer;
        if (MINECRAFT_VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
            bodyRotateContainer = super.newContainer(PacketType.Play.Server.ENTITY_TELEPORT);

            final Location location = super.fakeEntity.getLocation();
            bodyRotateContainer.getIntegers()
                    .write(1, (int) Math.floor(location.getX() * 32.0D))
                    .write(2, (int) Math.floor(location.getY() * 32.0D))
                    .write(3, (int) Math.floor(location.getZ() * 32.0D));
        } else {
            bodyRotateContainer = super.newContainer(PacketType.Play.Server.ENTITY_LOOK);
        }

        bodyRotateContainer.getBytes()
                .write(0, yawAngle)
                .write(1, pitchAngle);

        final PacketContainer lookContainer = super.newContainer(PacketType.Play.Server.ENTITY_LOOK);
        lookContainer.getBytes()
                .write(3, yawAngle)
                .write(4, pitchAngle);

        bodyRotateContainer.getBooleans().write(0, true);

        return this;
    }

    @NotNull
    public NPCRotationModifier queueBodyRotation(float yaw, float pitch) {
        byte yawAngle = (byte) (int) (yaw * 256F / 360F);
        byte pitchAngle = (byte) (int) (pitch * 256F / 360F);


        final PacketContainer bodyRotateContainer = super.newContainer(PacketType.Play.Server.ENTITY_LOOK);
        if (MINECRAFT_VERSION.newerThan(Minecraft.Version.v1_9_R2)) {
            Location location = super.fakeEntity.getLocation();
            bodyRotateContainer.getIntegers()
                    .write(1, (int) Math.floor(location.getX() * 32.0D))
                    .write(2, (int) Math.floor(location.getY() * 32.0D))
                    .write(3, (int) Math.floor(location.getZ() * 32.0D));
        }

        bodyRotateContainer.getBytes()
                .write(3, yawAngle)
                .write(4, pitchAngle);

        bodyRotateContainer.getBooleans().write(0, true);

        return this;
    }

    public NPCRotationModifier queueLookAt(@NotNull Location location) {
        final double xDifference = location.getX() - super.fakeEntity.getLocation().getX();
        final double yDifference = location.getY() - super.fakeEntity.getLocation().getY();
        final double zDifference = location.getZ() - super.fakeEntity.getLocation().getZ();

        final double r = Math
                .sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2) + Math.pow(zDifference, 2));
        float yaw = (float) (-Math.atan2(xDifference, zDifference) / Math.PI * 180D);
        yaw = yaw < 0 ? yaw + 360 : yaw;
        float pitch = (float) (-Math.asin(yDifference / r) / Math.PI * 180D);
        return this.queueRotate(yaw, pitch);
    }
}
