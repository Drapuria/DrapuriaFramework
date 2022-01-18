package net.drapuria.framework.bukkit.util.inventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class TitleUpdater {

    private static Method getHandle, sendPacket;
    private static Field activeContainerField, windowIdField, playerConnectionField;
    private static Constructor<?> chatMessageConstructor, packetPlayOutOpenWindowConstructor;

    static {
        try {
            getHandle = getObcClass("entity.CraftPlayer").getMethod("getHandle");
            chatMessageConstructor = getNmsClass("ChatMessage").getConstructor(String.class, Object[].class);
            Class<?> nmsPlayer = getNmsClass("EntityPlayer");
            activeContainerField = nmsPlayer.getField("activeContainer");
            windowIdField = getNmsClass("Container").getField("windowId");
            playerConnectionField = nmsPlayer.getField("playerConnection");
            packetPlayOutOpenWindowConstructor = getNmsClass("PacketPlayOutOpenWindow").getConstructor(Integer.TYPE, String.class, getNmsClass("IChatBaseComponent"), Integer.TYPE);
            sendPacket = getNmsClass("PlayerConnection").getMethod("sendPacket", getNmsClass("Packet"));
            // SeverSpecs.getNmsClass(nmsClassName) can be replaced with Class.forName("net.minecraft.server." + VERSION + "." + nmsClassName)
        } catch (NoSuchMethodException | SecurityException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getOBCPackageName() {
        return "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public static Class<?> getObcClass(String clazz) throws ClassNotFoundException {
        return getClassByName(getOBCPackageName() + "." + clazz);
    }

    public static Class<?> getClassByName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    private static Class<?> getNmsClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static void update(final Player p, String title) {
        if (p.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("container.crafting")) return;
        try {
            Object handle = getHandle.invoke(p);
            Object message = chatMessageConstructor.newInstance(title, new Object[0]);
            Object container = activeContainerField.get(handle);
            Object windowId = windowIdField.get(container);
            Object packet = packetPlayOutOpenWindowConstructor.newInstance(windowId, "minecraft:chest", message, p.getOpenInventory().getTopInventory().getSize());
            Object playerConnection = playerConnectionField.get(handle);
            sendPacket.invoke(playerConnection, packet);
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        p.updateInventory();
    }
}