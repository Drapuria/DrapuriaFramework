package net.drapuria.framework.bukkit.protocol.packet.wrapper;

import lombok.Getter;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.minecraft.NettyClassResolver;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.NMSClassResolver;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.OBCClassResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.ChatComponentWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.GameProfileWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.MethodWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.PacketWrapper;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.List;

@Getter
public class WrappedPacket implements WrapperPacketReader {

    private static Class<?> NMS_ITEM_STACK;
    private static MethodWrapper<ItemStack> ITEM_COPY_OF_METHOD;

    public static final NMSClassResolver NMS_CLASS_RESOLVER = new NMSClassResolver();
    public static final OBCClassResolver CRAFT_CLASS_RESOLVER = new OBCClassResolver();
    public static final NettyClassResolver NETTY_CLASS_RESOLVER = new NettyClassResolver();

    public static void init() {
        try {
            NMS_ITEM_STACK = NMS_CLASS_RESOLVER.resolve("ItemStack");

            Class<?> type = CRAFT_CLASS_RESOLVER.resolve("inventory.CraftItemStack");
            ITEM_COPY_OF_METHOD = new MethodWrapper<>(type.getMethod("asBukkitCopy", NMS_ITEM_STACK));
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected final Player player;

    protected PacketWrapper packet;
    private Class<?> packetClass;

    public WrappedPacket() {
        this(null);
    }

    public WrappedPacket(final Object packet) {
        this(null, packet);
    }

    public WrappedPacket(final Player player, final Object packet) {
        if(packet == null) {
            this.player = null;
            return;
        }
        this.packetClass = packet.getClass();

        if (packet.getClass().getSuperclass().equals(PacketTypeClasses.Client.FLYING)) {
            packetClass = PacketTypeClasses.Client.FLYING;
        } else if (packet.getClass().getSuperclass().equals(PacketTypeClasses.Server.ENTITY)) {
            packetClass = PacketTypeClasses.Server.ENTITY;
        }

        this.player = player;
        this.packet = new PacketWrapper(packet);
        setup();
    }

    protected void setup() {

    }

    public <T extends WrappedPacket> T wrap(Class<T> type) {
        try {
            return type.cast(this);
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Couldn't convert current wrapper " + this.getClass().getSimpleName() + " to " + type.getSimpleName());
        }
    }

    public World getWorld() {
        return this.player != null ? this.player.getWorld() : null;
    }

    @Override
    public boolean readBoolean(int index) {
        return readObject(index, boolean.class);
    }

    @Override
    public byte readByte(int index) {
        return readObject(index, byte.class);
    }

    @Override
    public short readShort(int index) {
        return readObject(index, short.class);
    }

    @Override
    public int readInt(int index) {
        return readObject(index, int.class);
    }

    @Override
    public long readLong(int index) {
        return readObject(index, long.class);
    }

    @Override
    public float readFloat(int index) {
        return readObject(index, float.class);
    }

    @Override
    public double readDouble(int index) {
        return readObject(index, double.class);
    }

    @Override
    public ItemStack readItemStack(int index) {
        return ITEM_COPY_OF_METHOD.invoke(null, readObject(index, NMS_ITEM_STACK));
    }

    @Override
    public GameProfileWrapper readGameProfile(int index) {
        return new GameProfileWrapper(this.readObject(index, Minecraft.GAME_PROFILE_TYPE));
    }

    @Override
    public <T> List<T> readList(int index) {
        return (List<T>) readObject(index, List.class);
    }

    @Override
    public ChatComponentWrapper readChatComponent(int index) {
        return ChatComponentWrapper.fromHandle(this.readObject(index, Minecraft.getIChatBaseComponentClass()));
    }

    @Override
    public <T> T readObject(int index, Class<T> type) {
        return this.packet.getPacketValueByIndex(type, index);
    }

    @Override
    public Object readAnyObject(int index) {
        return this.packet.getFieldByIndex(null, index);
    }

    @Override
    public String readString(int index) {
        return readObject(index, String.class);
    }

    public void validBasePacketExists() {
        if (this.packet != null) {
            throw new UnsupportedOperationException("The packet " + this.getClass().getSimpleName() + " does not have a base packet!");
        }
    }

    public void set(Class<?> type, int index, Object object) {
        this.packet.setFieldByIndex(type, index, object);
    }
}
