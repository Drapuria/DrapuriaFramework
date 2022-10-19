package net.drapuria.framework.bukkit.fake.hologram.helper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.experimental.UtilityClass;
import net.drapuria.framework.util.Stacktrace;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@UtilityClass
public class PacketHelper {

    private Method getNmsCopy;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf(".") + 1);
        try {
            getNmsCopy = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            getNmsCopy = null;
            Stacktrace.print(e.getMessage(), e);
        }
    }

    public PacketContainer itemArmorStandAttach(int itemEntityId, int armorStandEntityId) {
        final PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ATTACH_ENTITY);
        packet.getIntegers().write(0, 0);
        packet.getIntegers().write(1, itemEntityId);
        packet.getIntegers().write(2, armorStandEntityId);
        return packet;
    }

    public PacketContainer itemArmorStandMeta(int entityId) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(10, new WrappedWatchableObject(10, (byte) 31), true);
        watcher.setObject(0, new WrappedWatchableObject(0, (byte) 32), true);
        watcher.setObject(3, new WrappedWatchableObject(3, (byte) 0), true);
        packet.getIntegers().write(0, entityId);
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        return packet;
    }

    public PacketContainer itemSpawn(int entityId, double x, double y, double z) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getIntegers().write(0, entityId);
        packet.getIntegers().write(1, (int) Math.floor(x * 32D));
        packet.getIntegers().write(2, (int) Math.floor(y * 32D));
        packet.getIntegers().write(3, (int) Math.floor(z * 32D));
        packet.getIntegers().write(4, 0);
        packet.getIntegers().write(5, 0);
        packet.getIntegers().write(6, 0);
        packet.getIntegers().write(7, 0);
        packet.getIntegers().write(8, 0);
        packet.getIntegers().write(9, 2);
        packet.getIntegers().write(10, 1);
        return packet;
    }

    public PacketContainer itemMeta(int entityId, ItemStack itemStack) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        try {
            watcher.setObject(10, getNmsCopy.invoke(null, itemStack), true);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        packet.getIntegers().write(0, entityId);
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        return packet;
    }

    public PacketContainer armorStandSpawn(int entityId, double x, double y, double z) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getIntegers().write(0, entityId);
        packet.getIntegers().write(1, (int) Math.floor(x * 32D));
        packet.getIntegers().write(2, (int) Math.floor(y * 32D));
        packet.getIntegers().write(3, (int) Math.floor(z * 32D));
        packet.getIntegers().write(4, 0);
        packet.getIntegers().write(5, 0);
        packet.getIntegers().write(6, 0);
        packet.getIntegers().write(7, 0);
        packet.getIntegers().write(8, 0);
        packet.getIntegers().write(9, 78);
        packet.getIntegers().write(10, 0);
        return  packet;
    }

    public PacketContainer armorStandMeta(int entityId, String text) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(10, (byte) 31, true);
        watcher.setObject(0, (byte) 32, true);
        watcher.setObject(3, (byte) 1, true);
        watcher.setObject(2, text);
        packet.getIntegers().write(0, entityId);
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        return  packet;
    }

    public PacketContainer entityMove(int entityid, byte changeX, byte changeY, byte changeZ) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);
        packet.getIntegers().write(0, entityid);
        packet.getBytes().write(0, changeX);
        packet.getBytes().write(1, changeY);
        packet.getBytes().write(2, changeZ);
        return packet;
    }

    public PacketContainer entityTeleport(int entityId, double newX, double newY, double newZ) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        packet.getIntegers().write(1, (int) (newX * 32.0D));
        packet.getIntegers().write(2, (int) (newY * 32.0D));
        packet.getIntegers().write(3, (int) (newZ * 32.0D));
        return packet;
    }

    public PacketContainer entityDestroy(int... entityId) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, entityId);
        return packet;
    }

    public void sendPackets(Player player, PacketContainer... packets) {
        for (PacketContainer packet : packets) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}