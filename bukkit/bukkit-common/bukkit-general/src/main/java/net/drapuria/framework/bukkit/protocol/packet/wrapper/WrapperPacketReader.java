package net.drapuria.framework.bukkit.protocol.packet.wrapper;

import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ChatComponentWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.GameProfileWrapper;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public interface WrapperPacketReader {

    boolean readBoolean(int index);

    byte readByte(int index);

    short readShort(int index);

    int readInt(int index);

    long readLong(int index);

    float readFloat(int index);

    double readDouble(int index);

    ItemStack readItemStack(int index);

    ChatComponentWrapper readChatComponent(int index);

    GameProfileWrapper readGameProfile(int index);

    <T> List<T> readList(int index);

    <T> T readObject(int index, Class<T> type);

    Object readAnyObject(int index);

    String readString(int index);
}
