package net.drapuria.framework.bukkit.fake.entity.modifier;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import net.drapuria.framework.bukkit.fake.entity.FakeEntity;
import net.drapuria.framework.bukkit.protocol.protocollib.ProtocolLibService;
import net.drapuria.framework.bukkit.reflection.minecraft.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FakeEntityModifier<T extends FakeEntity> {

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public static final MinecraftVersion MINECRAFT_VERSION = MinecraftVersion.getVersion();
    private static ProtocolLibService protocolService = ProtocolLibService.getService;

    private final List<PacketContainer> packetContainerList = new CopyOnWriteArrayList<>();

    protected final T fakeEntity;

    public FakeEntityModifier(@NotNull final T fakeEntity) {
        this.fakeEntity = fakeEntity;
    }

    protected PacketContainer newContainer(@NotNull PacketType packetType) {
        return this.newContainer(packetType, true);
    }

    protected PacketContainer newContainer(@NotNull PacketType packetType, boolean withEntityId) {
        final PacketContainer packetContainer = new PacketContainer(packetType);
        if (withEntityId)
            packetContainer.getIntegers().write(0, this.fakeEntity.getEntityId());
        this.packetContainerList.add(packetContainer);
        return packetContainer;
    }

    protected PacketContainer lastContainer() {
        return this.packetContainerList.isEmpty() ? null : this.packetContainerList.get(packetContainerList.size() - 1);
    }

    protected PacketContainer lastContainer(final PacketContainer def) {
        final PacketContainer lastContainer = this.lastContainer();
        return lastContainer == null ? def : lastContainer;
    }

    public void send(@NotNull Iterable<? extends Player> players) {
        this.send(players, false);
    }

    public void send(boolean createClone) {
        this.send(Bukkit.getOnlinePlayers(), createClone);
    }

    public void send(@NotNull Iterable<? extends Player> players, final boolean createClone) {
        if (this.packetContainerList.isEmpty())
            return;
        players.forEach(player ->
                packetContainerList.forEach(packetContainer ->
                        protocolService.sendPacket(player, createClone ? packetContainer.shallowClone() : packetContainer)));
        packetContainerList.clear();
    }

    protected void add(final PacketContainer packetContainer) {
        this.packetContainerList.add(packetContainer);
    }

    public void send(@NotNull Player... players) {
        this.send(Arrays.asList(players));
    }

    public void send(boolean createClone, @NotNull Player... targetPlayers) {
        this.send(Arrays.asList(targetPlayers), createClone);
    }
}
