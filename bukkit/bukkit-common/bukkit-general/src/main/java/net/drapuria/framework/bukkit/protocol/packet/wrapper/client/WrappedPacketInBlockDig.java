package net.drapuria.framework.bukkit.protocol.packet.wrapper.client;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.PacketDirection;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketType;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.annotation.AutowiredWrappedPacket;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.other.EnumDirection;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.minecraft.MinecraftVersion;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ObjectWrapper;
import net.drapuria.framework.bukkit.util.BlockPosition;


@Getter
@AutowiredWrappedPacket(value = PacketType.Client.BLOCK_DIG, direction = PacketDirection.READ)
public final class WrappedPacketInBlockDig extends WrappedPacket {
    private static Class<?> blockDigClass, blockPositionClass, enumDirectionClass, digTypeClass;
    private BlockPosition blockPosition;
    private EnumDirection direction;
    private PlayerDigType digType;
    public WrappedPacketInBlockDig(Object packet) {
        super(packet);
    }

    public static void init() {
        blockDigClass = PacketTypeClasses.Client.BLOCK_DIG;
        try {
            if (MinecraftVersion.VERSION.newerThan(Minecraft.Version.v1_7_R1)) {
                blockPositionClass = NMS_CLASS_RESOLVER.resolve("BlockPosition");
                enumDirectionClass = NMS_CLASS_RESOLVER.resolve("EnumDirection");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (!MinecraftVersion.VERSION.equals(Minecraft.Version.v1_7_R1)) {
            try {
                digTypeClass = NMS_CLASS_RESOLVER.resolve("EnumPlayerDigType");
            } catch (ClassNotFoundException e) {
                //It is probably a subclass
                digTypeClass = NMS_CLASS_RESOLVER.resolveSilent(blockDigClass.getSimpleName() + "$EnumPlayerDigType");
            }
        }
    }

    @Override
    protected void setup() {
        EnumDirection enumDirection = null;
        PlayerDigType enumDigType = null;
        int x = 0, y = 0, z = 0;
        //1.7.10
        if (MinecraftVersion.VERSION.olderThan(Minecraft.Version.v1_8_R1)) {
            enumDigType = PlayerDigType.values()[new FieldResolver(blockDigClass).resolve(int.class, 4).get(null)];

            x = readInt(0);
            y = readInt(1);
            z = readInt(2);

            enumDirection = null;
        } else {
            //1.8+
            final Object blockPosObj = readObject(0, blockPositionClass);
            final Enum<?> enumDirectionObj = (Enum<?>) readObject(0, enumDirectionClass);
            final Enum<?> digTypeObj = (Enum<?>) readObject(0, digTypeClass);

            ObjectWrapper objectWrapper = new ObjectWrapper(blockPosObj);
            x = objectWrapper.getFieldByIndex(int.class, 0);
            y = objectWrapper.getFieldByIndex(int.class, 1);
            z = objectWrapper.getFieldByIndex(int.class, 2);

            enumDirection = EnumDirection.valueOf(enumDirectionObj.name());
            enumDigType = PlayerDigType.valueOf(digTypeObj.name());
        }
        this.blockPosition = new BlockPosition(x, y, z, this.player.getWorld().getName());
        if (enumDirection == null) {
            this.direction = EnumDirection.NULL;
        } else {
            this.direction = enumDirection;
        }
        this.digType = enumDigType;
    }

    public enum PlayerDigType {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        SWAP_HELD_ITEMS,
        SWAP_ITEM_WITH_OFFHAND,
        WRONG_PACKET
    }


}
