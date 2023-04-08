package net.drapuria.framework.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemStackSerializer  {
    public ItemStackSerializer() {
    }

    public static ItemStack deserializeItemStack(String data) {
        return null;
    }

    public static String serializeItemStack(ItemStack item) {


        return null;
    }

    public static String toBase64List(ItemStack[] items) {
        return null;
    }

    public static ItemStack[] fromBase64List(String items) {
        return null;
    }

    private static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    private static Class<?> getOBClass(String name) {
        return null;
    }
}
