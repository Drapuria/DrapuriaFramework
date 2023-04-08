package net.drapuria.framework.bukkit.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;

import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

@Component
public class ItemStackSerializer implements ObjectSerializer<ItemStack, String> {
    public ItemStackSerializer() {
    }

    public static ItemStack deserializeItemStack(String data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream((new BigInteger(data, 32)).toByteArray())) {
            return ItemStack.deserializeBytes(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String serializeItemStack(ItemStack item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.writeBytes(item.serializeAsBytes());

            return (new BigInteger(1, outputStream.toByteArray())).toString(32);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * A method to serialize an {@link ItemStack} array to Base64 String.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     * @throws IllegalStateException
     */
    public static String toBase64List(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                if (item != null) {
                    dataOutput.writeObject(item.serializeAsBytes());
                } else {
                    dataOutput.writeObject(null);
                }
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * Gets an array of ItemStacks from Base64 string.
     *
     * @param data Base64 string to convert to ItemStack array.
     * @return ItemStack array created from the Base64 string.
     * @throws IOException
     */
    public static ItemStack[] fromBase64List(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int Index = 0; Index < items.length; Index++) {
                byte[] stack = (byte[]) dataInput.readObject();

                if (stack != null) {
                    items[Index] = ItemStack.deserializeBytes(stack);
                } else {
                    items[Index] = null;
                }
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    @Override
    public String serialize(ItemStack input) {
        return serializeItemStack(input);
    }

    @Override
    public ItemStack deserialize(String output) {
        return deserializeItemStack(output);
    }

    @Override
    public Class<ItemStack> inputClass() {
        return ItemStack.class;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }
}
